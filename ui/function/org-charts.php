<?php
function print_org_chart($data)
{
    $colleagues = count($data["colleagues"]);
    $subordinates = count($data["subordinates"]);
    echo "<table style='width:100%'>";

    // For manager
    if (array_key_exists("manager", $data) && is_array($data["manager"])) {
        echo "<tr>";
        print_employee_card($data["manager"], $colleagues + $subordinates);
        echo "</tr>";
    }

    // Line
    echo "<tr>";
    echo "<td colspan='" . (($colleagues + $subordinates) * 2) . "'>";
    echo "<table class='line'><tr><th class='right width-50'></th><th class='width-50'></th></tr></table>";
    echo "</td>";
    echo "</tr>";

    $size = $colleagues + $subordinates;
    $tdWidth = ((100) / (($colleagues + $subordinates) * 2));
    echo "<tr>";
    echo "<td colspan='" . ($subordinates * 2) . "'>
            <table class='line' style='margin:-5 3;'><tr>
            <th class='right width-50'></th>
            <th class='top width-50'></th>
            </tr></table>
        </td>";
    for ($j = 1; $j < $colleagues; $j++) {
        echo '<th class="right top" width="' . $tdWidth . '%"></th>';
        echo '<th class="top" width="' . $tdWidth . '%"></th>';
    }
    echo '<th class="right top" width="' . $tdWidth . '%"></th>';
    echo '<th width="' . $tdWidth . '%"></th>';
    echo '</tr>';


    // For employee and colleagues
    echo "<tr>";
    echo print_employee_card($data["employee"], $subordinates, true);
    if (count($data["colleagues"]) > 0) {
        foreach ($data["colleagues"] as $subordinate) {
            print_employee_card($subordinate, 1);
        }
    }
    echo "</tr>";

    //Line
    $size = $subordinates;
    $tdWidth = ((100) / (($colleagues + $subordinates) * 2));
    echo '<tr>';
    echo '<td colspan="' . ($size * 2) . '">';
    echo '<table class="line"><tr><th class="right width-50"></th><th class="width-50"></th></tr></table>';
    echo '</td>';
    echo '</tr>';

    echo '<tr>';
    echo '<th class="right" width="' . $tdWidth . '%"></th>';
    echo '<th class="top" width="' . $tdWidth . '%"></th>';
    for ($j = 1; $j < $size - 1; $j++) {
        echo '<th class="right top" width="' . $tdWidth . '%"></th>';
        echo '<th class="top" width="' . $tdWidth . '%"></th>';
    }
    echo '<th class="right top" width="' . $tdWidth . '%"></th>';
    echo '<th width="' . $tdWidth . '%"></th>';
    echo '</tr>';


    // For subordinates
    if (count($data["subordinates"]) > 0) {
        echo "<tr>";
        foreach ($data["subordinates"] as $subordinate) {
            print_employee_card($subordinate, 1);
        }
        echo "</tr>";
    }


    echo "</table>";
}

function print_employee_card(array $data, $subordinates, $self_card = false)
{
    echo "<td colspan='" . ($subordinates * 2) . "'>
            <table class='" . ($self_card ? "self-card " : "others-card ") . "employee-box'><tr>
                <td class='card-content'>" . $data["id"] . " - " . $data["name"] . "</td>
            </tr><tr>
                <td class='card-content'>(" . $data["jobTitle"] . ")</td>
            </tr></table>
        </td>";
}