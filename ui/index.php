<?php
require_once 'includes/config.php';
require_once 'function/org-charts.php';
require_once 'function/util.php';

if (isset($_REQUEST['employee_id']) && !empty($_REQUEST['employee_id'])) {
    try {
        $json_data = getEmployeeDetails($_REQUEST['employee_id']);
        $data = json_decode($json_data, true);
    } catch (Exception $e) {
        header("Location: index.php?w=" . $e->getMessage());
    }
}
?>
<!DOCTYPE >
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="assets/org-chart.css"/>
    <title>Home Page</title>
</head>
<body>
<div class="container">
    <div class="text-center h1">Organization Chart</div>

    <?php
    if (isset($_REQUEST['w']) && !empty($_REQUEST['w'])) { ?>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <strong>Error : </strong><?php echo $_GET['w']; ?>
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        </div><?php
    }
    if (!isset($_REQUEST['employee_id']) || empty($_REQUEST['employee_id'])) { ?>
        <div class="">
            <form method="post">
                <div class="form-row align-items-center">
                    <div class="col-auto">
                        <label for="employeeId" class="sr-only"> Employee ID : </label>
                        <input type="number" class="form-control" name="employee_id" id="employeeId"
                               placeholder="Enter Employee ID"
                               required>
                    </div>
                    <div class="col-auto">
                        <input type="submit" class="btn btn-primary" value="find">
                    </div>
                </div>
            </form>
        </div>
        <?php
    } else { ?>
        <div class="orgchart">
            <?php print_org_chart($data); ?>
        </div>
        <br><br>
        <div class="text-center">
            <a href="index.php"><--Go Back</a>
        </div><?php
    }
    ?>
</div>
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
        integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
        integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
        crossorigin="anonymous"></script>
</body>
</html>