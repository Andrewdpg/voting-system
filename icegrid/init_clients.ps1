$remote_directory = "andres-parra"
$password = "swarch"

# Lista de dispositivos
$client_hosts = @("xhgrid11", "xhgrid13")
$registry = "xhgrid3"

# Archivo de entrada y salida
$client_jar = "../client/build/libs/client.jar"

# Host key fingerprints
$hostkey = "ssh-ed25519 255 SHA256:tvUus3tmIBfXgIDVZsiNAwd/rzkHWxMdLQxg5wZ+JFE"

foreach ($client_host in $client_hosts) {
    Write-Output "Copiando archivos a $client_host"
    & plink -batch -pw $password -hostkey $hostkey "swarch@$client_host" "rm -rf $remote_directory; mkdir -p $remote_directory"
    & pscp -batch -pw $password -hostkey $hostkey $client_jar "swarch@${client_host}:$remote_directory/"
    Write-Output "Ejecutando cliente en $client_host"
    & plink -batch -pw $password -hostkey $hostkey "swarch@$client_host" "killall java; cd $remote_directory; nohup java -jar client.jar ${registry} > client.log 2>&1 &"
}