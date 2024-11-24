$unique_id = "andres-parra"
$remote_directory = "andres-parra"
$password = "swarch"

# Lista de dispositivos
$worker_hosts = @("xhgrid10")
$database_hosts = @("xhgrid1")
$registry = "xhgrid2"

# Archivo de entrada y salida
$application_template = "application.xml"
$node_template = "node.cfg"
$registry_template = "registry.cfg"

$worker_jar = "../worker/build/libs/worker.jar"
$database_jar = "../database/build/libs/database.jar"

$application_file = "output.xml"
$node_config_file = "node.cfg"

# Host key fingerprints
$hostkey = "ssh-ed25519 255 SHA256:tvUus3tmIBfXgIDVZsiNAwd/rzkHWxMdLQxg5wZ+JFE"

# Ejecutar el registro en la máquina remota
Write-Output "Ejecutando registro en $registry"
& plink -batch -pw $password -hostkey $hostkey "swarch@$registry" "rm -rf $remote_directory; mkdir -p $remote_directory"
& pscp -batch -pw $password -hostkey $hostkey $registry_template "swarch@${registry}:$remote_directory/registry.cfg"
& plink -batch -pw $password -hostkey $hostkey "swarch@$registry" "killall icegridregistry; cd $remote_directory; mkdir -p data/registry; mkdir -p data/logs; touch data/logs/icegridregistry.log; nohup icegridregistry --Ice.Config=registry.cfg > icegrid.log 2>&1 &"

# Generar la estructura XML para cada host
$nodes = ""
foreach ($db_host in $database_hosts) {
    $nodes += @"
        <node name="$unique_id$db_host">
            <server-instance template="Database" index="1" host="$unique_id$db_host"/>
            <server-instance template="Database" index="2" host="$unique_id$db_host"/>
        </node>

"@
    Write-Output "Copiando archivos a $db_host"
    $config_content = Get-Content $node_template | ForEach-Object { $_ -replace "{registry}", $registry -replace "{host}", "$unique_id$db_host" }
    $temp_file = [System.IO.Path]::GetTempFileName()
    Set-Content -Path $temp_file -Value $config_content
    & plink -batch -pw $password -hostkey $hostkey "swarch@$db_host" "rm -rf $remote_directory; mkdir -p $remote_directory"
    & pscp -batch -pw $password -hostkey $hostkey $temp_file "swarch@${db_host}:$remote_directory/$node_config_file"
    Remove-Item $temp_file
    & pscp -batch -pw $password -hostkey $hostkey $database_jar "swarch@${db_host}:$remote_directory/"
    Write-Output "Ejecutando nodo en $db_host"
    & plink -batch -pw $password -hostkey $hostkey "swarch@$db_host" "killall icegridnode; cd $remote_directory; mkdir -p data; touch data/Database-$unique_id$worker_host.out; nohup icegridnode --Ice.Config=$node_config_file  > icegrid.log 2>&1 &"
}

foreach ($worker_host in $worker_hosts) {
    $nodes += @"
        <node name="$unique_id$worker_host">
            <server-instance template="Worker" index="1" host="$unique_id$worker_host"/>
            <server-instance template="Worker" index="2" host="$unique_id$worker_host"/>
        </node>

"@
    Write-Output "Copiando archivos a $worker_host"
    $config_content = Get-Content $node_template | ForEach-Object { $_ -replace "{registry}", $registry -replace "{host}", "$unique_id$worker_host" }
    $temp_file = [System.IO.Path]::GetTempFileName()
    Set-Content -Path $temp_file -Value $config_content
    & plink -batch -pw $password -hostkey $hostkey "swarch@$worker_host" "rm -rf $remote_directory; mkdir -p $remote_directory"
    & pscp -batch -pw $password -hostkey $hostkey $temp_file "swarch@${worker_host}:$remote_directory/$node_config_file"
    Remove-Item $temp_file
    & pscp -batch -pw $password -hostkey $hostkey $worker_jar "swarch@${worker_host}:$remote_directory/"
    Write-Output "Ejecutando nodo en $worker_host"
    & plink -batch -pw $password -hostkey $hostkey "swarch@$worker_host" "killall icegridnode; cd $remote_directory; mkdir -p data;  data/Worker-$unique_id$worker_host.out; nohup icegridnode --Ice.Config=$node_config_file  > icegrid.log 2>&1 &"
}

# Usar sed para reemplazar el placeholder con la estructura generada
(Get-Content $application_template) -replace "{--hosts--}", $nodes | Set-Content $application_file

# Copiar el archivo de configuración a la máquina remota
Write-Output "Copiando archivos a $registry"
& pscp -batch -pw $password -hostkey $hostkey $application_file "swarch@${registry}:$remote_directory/application.xml"
Remove-Item $application_file

& plink -batch -pw $password -hostkey $hostkey "swarch@$registry" "cd $remote_directory; icegridadmin --username any --password any --Ice.Config=registry.cfg -e 'application add application.xml'"
& plink -batch -pw $password -hostkey $hostkey "swarch@$registry" "cd $remote_directory; icegridadmin --username any --password any --Ice.Config=registry.cfg -e 'application update application.xml'"