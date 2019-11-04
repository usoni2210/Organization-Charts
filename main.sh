#!/bin/bash

clear

echo "Starting EMS Shell Script v1.0.3"
echo ""

# Variables
serverHost=${SERVER_HOST:="localhost"}
serverPort=${SERVER_PORT:=8080}
apiPrefix=${SERVER_API_PREFIX:="rest"}
apiUrl="http://$serverHost:$serverPort/$apiPrefix"
jq="jq "
test_num=0
test_flag=()
result_passes=0
result_fails=0
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Helper functions
function newTestCase() {
  test_num=$((test_num+1))
  description=$1
}

function printTestCase() {
  if [ "$1" == "true" ]; then
    echo -e "${GREEN}[o]${NC} Test Case $test_num: $description"
    result_passes=$((result_passes+1))
    test_flag[$test_num]="1"
  else
    echo -e "${RED}[x]${NC} Test Case $test_num: $description"
    result_fails=$((result_fails+1))
    test_flag[$test_num]="0"
  fi
}

function printResults() {
  echo ""
  echo "------------------------------------------------------------"
  echo "Passed: ($result_passes / $test_num) | Failed: ($result_fails / $test_num)"
  echo "------------------------------------------------------------"
}

function formatResponse() {
  status=$(echo "$response" | grep "RESP_CODE:")
  body="${response%??????????????}"
  statusCode="${status:10}"
}

function getOperation() {
  response=$(curl --request GET -sw "\nRESP_CODE:%{response_code}" \
      --url "$apiUrl/employees")
   formatResponse
}

function postOperation() {
  response=$(curl --request POST -sw "\nRESP_CODE:%{response_code}" \
      --header "Content-Type: application/json" \
      --data "$1" \
      --url "$apiUrl/employees")
   formatResponse
}

function getByIdOperation() {
  response=$(curl --request GET -sw "\nRESP_CODE:%{response_code}" \
      --url "$apiUrl/employees/$1")
   formatResponse
}

function deleteByIdOperation() {
  response=$(curl --request DELETE -sw "\nRESP_CODE:%{response_code}" \
      --url "$apiUrl/employees/$1")
   formatResponse
}

function putByIdOperation() {
  response=$(curl --request PUT -sw "\nRESP_CODE:%{response_code}" \
      --header "Content-Type: application/json" \
      --data "$2" \
      --url "$apiUrl/employees/$1")
   formatResponse
}

function shouldFailGetOperation() {
  getByIdOperation "$1"

  if [ "$statusCode" == "$2" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response code should have been \"$2\" but found \"$statusCode\""
    echo "Response body: $body"
    echo  ""
  fi
}

function shouldBadRequestGetOperation() {
  shouldFailGetOperation "$1" "400"
}

function shouldNotFoundGetOperation() {
  shouldFailGetOperation "$1" "404"
}

function shouldFailPostOperation() {
  postOperation "$1"

  if [ "$statusCode" == "$2" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response code should have been \"$2\" but found \"$statusCode\""
    echo "Response body: $body"
    echo  ""
  fi
}

function shouldBadRequestPostOperation() {
  shouldFailPostOperation "$1" "400"
}

function shouldUnsupportedMediaTypePostOperation() {
  shouldFailPostOperation "$1" "415"
}

function shouldFailDeleteOperation() {
  deleteByIdOperation "$1"

  if [ "$statusCode" == "$2" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response code should have been \"$2\" but found \"$statusCode\""
    echo "Response body: $body"
    echo  ""
  fi
}

function shouldBadRequestDeleteOperation() {
  shouldFailDeleteOperation "$1" "400"
}

function shouldNotFoundDeleteOperation() {
  shouldFailDeleteOperation "$1" "404"
}

function shouldFailPutOperation() {
  putByIdOperation "$1" "$2"

  if [ "$statusCode" == "$3" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response code should have been \"$3\" but found \"$statusCode\""
    echo "Response body: $body"
    echo  ""
  fi
}

function shouldBadRequestPutOperation() {
    shouldFailPutOperation "$1" "$2" "400"
}

function shouldNotFoundPutOperation() {
    shouldFailPutOperation "$1" "$2" "404"
}

function shouldUnsupportedMediaTypePutOperation() {
    shouldFailPutOperation "$1" "$2" "415"
}

names=( "Thor" "Iron Man" "Hulk" "Captain America" "War Machine" "Vision" "Falcon" "Ant Man" "Spider Man" "Black Widow" )
jobs=( "Director" "Manager" "Lead" "Manager" "QA" "DevOps" "Developer" "Lead" "Intern" "Developer" )
managers=( -1 1 1 1 2 2 4 4 2 3 )
function generateData() {
    for i in {0..9}
    do
      postOperation  "{ \"name\": \"${names[i]}\", \"jobTitle\": \"${jobs[i]}\", \"managerId\": ${managers[i]} }"
      if [ "$statusCode" != "201" ]; then
        printTestCase false
        echo "Faild to add initial data, response status code should have been \"201\" but found \"$statusCode\""
        echo "Response body: $body"
        echo ""
        printResults
        exit
      fi
    done
}

# Initialize dummy data
newTestCase "Initialize with dummy data"
generateData
printTestCase true

newTestCase "Perform GET /employee"
getOperation

listSize=$(echo "$body" | $jq ". | length")

if [ "$listSize" == "10" ]; then
  printTestCase true
else
  printTestCase false
  echo "Response list size should be 10 but found $listSize"
  echo ""
fi

newTestCase "Check if employees list is soreted"
sortedArray=(1 4 2 8 3 10 7 6 5 9)
failedFlag=0
for i in {0..9}
do
  id=$(echo "$body" | $jq ".[$i].id")
  if [ "$id" != "${sortedArray[i]}" ]; then
    printTestCase false
    echo "List of employees was not printed in sorted order"
    failedFlag=1
    break
  fi
done

if [ "$failedFlag" == "0" ]; then
  printTestCase true
fi

newTestCase "Perform GET /employee/{id}"
getByIdOperation 2

id=$(echo "$body" | $jq ".id")
title=$(echo "$body" | $jq ".name")
jobTitle=$(echo "$body" | $jq ".jobTitle")
nestedId=$(echo "$body" | $jq ".employee.id")
nestedTitle=$(echo "$body" | $jq ".employee.name")
nestedJobTitle=$(echo "$body" | $jq ".employee.jobTitle")
manager=$(echo "$body" | $jq ".manager.id")
suboridnatesSize=$(echo "$body" | $jq ".subordinates | length")
colleaguesSize=$(echo "$body" | $jq ".colleagues | length")

if [ "$id" == "2" ] || [ "$nestedId" == "2" ]; then
  flagId=1
fi
if [ "$title" == "\"Iron Man\"" ] || [ "$nestedTitle" == "\"Iron Man\"" ]; then
  flagTitle=1
fi
if [ "$jobTitle" == "\"Manager\"" ] || [ "$nestedJobTitle" == "\"Manager\"" ]; then
  flagJobTitle=1
fi

if [ "$flagId" == "1" ] && [ "$flagTitle" == "1" ] && [ "$flagJobTitle" == "1" ] && [ "$manager" == "1" ] && [ "$suboridnatesSize" == "3" ] && [ "$colleaguesSize" == "2" ]; then
  printTestCase true
else
  printTestCase false
  if [ "$id" != "2" ]; then
    echo "Response should have ID \"2\" but found \"$id\""
  fi
  if [ "$title" != "\"Iron Man\"" ]; then
    echo "Response should have name \"Iron Man\" but found \"$title\""
  fi
  if [ "$jobTitle" != "\"Manager\"" ]; then
    echo "Response should have job title \"Manager\" but found \"$jobTitle\""
  fi
  if [ "$manager" != "1" ]; then
    echo "Response should have manager ID \"1\" but found \"$manager\""
  fi
  if [ "$suboridnatesSize" != "3" ]; then
    echo "Response should have suboridnates array of size \"3\" but found \"$suboridnatesSize\""
  fi
  if [ "$colleaguesSize" != "2" ]; then
    echo "Response should have colleagues array of size \"2\" but found \"$colleaguesSize\""
  fi
  echo "Response body: $body"
  echo ""
fi

newTestCase "Check if colleagues of employee are in sorted order"

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  printTestCase false
  echo "Cannot run this test as test $((test_num-1)) has failed"
  echo ""
else
  sortedArray=(4 3)

  for i in {0..1}
  do
    id=$(echo "$body" | $jq ".colleagues[$i].id")
    if [ "$id" != "${sortedArray[i]}" ]; then
      printTestCase false
      echo "Employee's colleagues was not printed in sorted order"
      failedFlag=1
      break
    fi
  done

  if [ "$failedFlag" == "0" ]; then
    printTestCase true
  fi
fi

newTestCase "Check if subordinates of employee are in sorted order"
getByIdOperation 1

sortedArray=(4 2 3)

if [ "${test_flag[$test_num-2]}" == "0" ]; then
  printTestCase false
  echo "Cannot run this test as test $((test_num-2)) has failed"
  echo ""
else
  for i in {0..2}
  do
    id=$(echo "$body" | $jq ".subordinates[$i].id")
    if [ "$id" != "${sortedArray[i]}" ]; then
      printTestCase false
      echo "Employee's subordinates was not printed in sorted order"
      failedFlag=1
      break
    fi
  done

  if [ "$failedFlag" == "0" ]; then
    printTestCase true
  fi
fi

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  printTestCase false
  echo "Cannot run many tests as test $((test_num-1)) has failed"
  echo ""
else
  newTestCase "Check if it fails to get due to id has no employees assigned"
  shouldNotFoundGetOperation 100

  newTestCase "Check if it fails to get due to incorrent type id"
  shouldBadRequestGetOperation "1.1"

  newTestCase "Check if it fails to get due to invalid id"
  shouldBadRequestGetOperation "-1"
fi

newTestCase "Perform POST /employee"

postOperation '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 1 }'
id=$(echo "$body"| $jq ".id")

if [ "$id" == "null" ] || [ "$id" == "" ]; then
  id=$(echo "$body" | $jq ".employee.id")
fi

getByIdOperation "$id"
title=$(echo "$body" | $jq ".name")
nestedTitle=$(echo "$body" | $jq ".employee.name")

if [ "$title" == "\"Black Panther\"" ] || [ "$nestedTitle" == "\"Black Panther\"" ]; then
  printTestCase true
else
  printTestCase false
  echo "Response name should be \"Black Panther\" but found $title"
  echo "Response body: $body"
  echo ""
fi

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  echo "Cannot run many tests as test $((test_num-1)) has failed"
  echo ""
else
  newTestCase "Check if it fails to post with empty body"
  shouldBadRequestPostOperation '{ }'

  newTestCase "Check if it fails to post with invalid body"
  shouldBadRequestPostOperation '<xml></xml>'

  newTestCase "Check if it fails to post with no employee name"
  shouldBadRequestPostOperation '{ "jobTitle": "Manager", "managerId": 1 }'

  newTestCase "Check if it fails to post with no job title"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "managerId": 1 }'

  newTestCase "Check if it fails to post with no manager ID"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Manager" }'

  newTestCase "Check if it fails to post with invalid manager ID"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": -1 }'

  newTestCase "Check if it fails to post when manager ID is not out"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 100 }'

  newTestCase "Check if it fails to post with invalid designation"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Senior Manager", "managerId": 1 }'

  newTestCase "Check if it fails to post with invalid name"
  shouldBadRequestPostOperation '{ "name": "#B1ack Pan1h3r", "jobTitle": "Manager", "managerId": 1 }'

  newTestCase "Check if it fails to post with same designation as manager"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 2 }'

  newTestCase "Check if it fails to post with higher designation than manager"
  shouldBadRequestPostOperation '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 9 }'
fi

newTestCase "Perform PUT /employee/{id}"
putByIdOperation 2 '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 1, "replace": true }'
title=$(echo "$body" | $jq ".name")
nestedTitle=$(echo "$body" | $jq ".employee.name")
id=$(echo "$body" | $jq ".id")

if [ "$id" == "null" ] || [ "$id" == "" ]; then
  id=$(echo "$body" | $jq ".employee.id")
fi

if [ "$title" == "\"Black Panther\"" ] || [ "$nestedTitle" == "\"Black Panther\"" ]; then
  printTestCase true
else
  printTestCase false
  echo "Response name should be \"Black Panther\" but found $title"
  echo "Response body: $body"
  echo ""
fi

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  echo "Cannot run many tests as test $((test_num-1)) has failed"
  echo ""
else
  newTestCase "Check if it updates employee name"
  putByIdOperation "$id" '{ "name": "Nick Fury", "jobTitle": "Manager", "managerId": 1, "replace": false }'
  title=$(echo "$body" | $jq ".name")
  nestedTitle=$(echo "$body" | $jq ".employee.name")

  if [ "$title" == "\"Nick Fury\"" ] || [ "$nestedTitle" == "\"Nick Fury\"" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response name should be \"Nick Fury\" but found $title"
    echo "Response body: $body"
    echo ""
  fi

  newTestCase "Check if it updates employee designation"
  putByIdOperation "$id" '{ "name": "Nick Fury", "jobTitle": "Lead", "managerId": 1, "replace": false }'
  jobTitle=$(echo "$body" | $jq ".jobTitle")
  nestedJobTitle=$(echo "$body" | $jq ".employee.jobTitle")

  if [ "$jobTitle" == "\"Lead\"" ] || [ "$nestedJobTitle" == "\"Lead\"" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response name should be \"Lead\" but found $title"
    echo "Response body: $body"
    echo ""
  fi

  newTestCase "Check if it updates employee manager"
  putByIdOperation "$id" '{ "name": "Nick Fury", "jobTitle": "Lead", "managerId": 4, "replace": false }'
  managerId=$(echo "$body" | $jq ".managerId")

  if [ "$managerId" == "4" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response name should be \"4\" but found $managerId"
    echo "Response body: $body"
    echo ""
  fi

  newTestCase "Check if it replaces employee without manager"
  putByIdOperation "$id" '{ "name": "Nick Fury", "jobTitle": "Lead", "replace": true }'
  managerId=$(echo "$body" | $jq ".managerId")
  id=$(echo "$body" | $jq ".id")

  if [ "$id" == "null" ] || [ "$id" == "" ]; then
    id=$(echo "$body" | $jq ".employee.id")
  fi

  newEmployee="$id"

  if [ "$managerId" == "4" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Response name should be \"4\" but found $managerId"
    echo "Response body: $body"
    echo ""
  fi

  newTestCase "Check if replaced employee's subordinates' manager ID has changed"

  if [ "${test_flag[$test_num-1]}" == "0" ]; then
    printTestCase false
    echo "Cannot run this test as test $((test_num-1)) has failed"
    echo ""
  else
    getByIdOperation 5
    managerId1=$(echo "$body"| $jq ".manager.id")

    getByIdOperation 6
    managerId2=$(echo "$body"| $jq ".manager.id")

    if [ "$managerId1" == "$newEmployee" ] && [ "$managerId2" == "$newEmployee" ]; then
      printTestCase true
    else
      printTestCase false
      echo "Manager ID of subordinates of deleted employee was not updated, should have been 12 but found $managerId1"
      echo "Response Body: $body"
      echo ""
    fi
  fi

  newTestCase "Check if it fails to put due to id has no employees assigned"
  shouldBadRequestPutOperation 100 '{ }'

  newTestCase "Check if it fails to put with invalid body"
  shouldBadRequestPutOperation 2 '<xml></xml>'

  newTestCase "Check if it fails to put due to incorrent type id"
  shouldBadRequestPutOperation "1.1" '{ }'

  newTestCase "Check if it fails to put due to invalid id"
  shouldBadRequestPutOperation "-1" '{ }'

  newTestCase "Check if it fails to put and update due to no body"
  shouldBadRequestPutOperation "$id" '{ }'

  newTestCase "Check if it fails to put and update with invalid manager ID"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": -100 }'

  newTestCase "Check if it fails to put and update when manager ID is not found"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 100 }'

  newTestCase "Check if it fails to put and update with invalid designation"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Senior Manager", "managerId": 1 }'

  newTestCase "Check if it fails to put and update with invalid name"
  shouldBadRequestPutOperation "$id" '{ "name": "#B1ack Pan1h3r", "jobTitle": "Manager", "managerId": 1 }'

  newTestCase "Check if it fails to put and update with same designation as manager"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 2 }'

  newTestCase "Check if it fails to put and update with lower designation than subordinates"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Developer", "managerId": 2 }'

  newTestCase "Check if it fails to put and update with higher designation than manager"
  shouldBadRequestPutOperation "$id" '{ "name": "Black Panther", "jobTitle": "Manager", "managerId": 9 }'

  newTestCase "Check if it fails to update director's designation"
  shouldBadRequestPutOperation 1 '{ "jobTitle": "Intern" }'
fi

newTestCase "Perform DELETE /employee/{id}"
deleteByIdOperation 3
status1="$statusCode"
body1="$body"

getByIdOperation 3
status2="$statusCode"

if [ "$status1" == "204" ] && [ "$status2" == "404" ]; then
  printTestCase true
else
  printTestCase false
  if [ "$status1" != "204" ]; then
    echo "DELETE response status should be \"204\" but found \"$status1\""
    echo "Response body: $body1"
  fi
  if [ "$status2" != "404" ]; then
    echo "GET response status should be \"404\" but found \"$status2\""
  fi
  echo ""
fi

newTestCase "Check if deleted employee's subordinates' manager ID has changed"

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  printTestCase false
  echo "Cannot run this test as test $((test_num-1)) has failed"
  echo ""
else
  getByIdOperation 10
  managerId1=$(echo "$body"| $jq ".manager.id")

  if [ "$managerId1" == "1" ]; then
    printTestCase true
  else
    printTestCase false
    echo "Manager ID of subordinates of deleted employee was not updated, should have been 12 but found $managerId1"
    echo "Response Body: $body"
    echo ""
  fi
fi

if [ "${test_flag[$test_num-1]}" == "0" ]; then
  printTestCase false
  echo "Cannot run many tests as test $((test_num-1)) has failed"
  echo ""
else
  newTestCase "Check if it fails to delete due to id has no employees assigned"
  shouldNotFoundDeleteOperation 100

  newTestCase "Check if it fails to delete due to incorrent type id"
  shouldBadRequestDeleteOperation "1.1"

  newTestCase "Check if it fails to delete due to invalid id"
  shouldBadRequestDeleteOperation "-1"

  newTestCase "Check if it fails to delete director"
  shouldBadRequestDeleteOperation 1
fi

newTestCase "Check if it fails to PATCH /employees/{id}"

response=$(curl --request PATCH -sw "\nRESP_CODE:%{response_code}" \
  --url "$apiUrl/employees/$1")
formatResponse

if [ "$statusCode" == "405" ]; then
  printTestCase true
else
  printTestCase false
  echo "Response should have been \"405\" but found \"$statusCode\""
  echo ""
fi

printResults