# Define the list of hosts
$hosts = @("xhgrid1", "xhgrid2", "xhgrid3", "xhgrid4", "xhgrid5", "xhgrid6", "xhgrid7", "xhgrid8", "xhgrid9", "xhgrid10", "xhgrid11", "xhgrid12", "xhgrid13", "xhgrid14", "xhgrid15", "xhgrid16", "xhgrid17", "xhgrid18", "xhgrid19", "xhgrid20", "xhgrid21", "xhgrid22")

# Loop through each host and perform a ping
foreach ($host_test in $hosts) {
    $pingResult = Test-Connection -ComputerName $host_test -Count 1 -Quiet
    if ($pingResult) {
        Write-Output "$host_test is reachable"
    } else {
        Write-Output "$host_test is not reachable"
    }
}