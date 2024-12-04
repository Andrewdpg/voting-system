#!/bin/bash

unique_id="andres-parra"
remote_directory="andres-parra"
password="swarch"

# Lista de dispositivos
worker_hosts=("xhgrid10")
database_hosts=("xhgrid1")
registry="xhgrid2"

# Archivo de entrada y salida
application_template="application.xml"
node_template="node.cfg"
registry_template="registry.cfg"

worker_jar="worker.jar"
database_jar="database.jar"

application_file="output.xml"
node_config_file="node.cfg"

# Ejecutar el registro en la máquina remota
echo "Ejecutando registro en $registry"
sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$registry" "mkdir -p $remote_directory"
sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$registry_template" "swarch@$registry:$remote_directory/registry.cfg"
sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$registry" "cd $remote_directory && mkdir -p data/registry && mkdir -p data/logs && touch data/logs/icegridregistry.log && icegridregistry --Ice.Config=registry.cfg &"

# Generar la estructura XML para cada host
nodes=""
for host in "${worker_hosts[@]}"; do
    nodes+="        <node name=\"$unique_id$host\">\n"
    nodes+="            <server-instance template=\"Worker\" index=\"1\" host=\"$unique_id$host\"\/>\n"
    nodes+="            <server-instance template=\"Worker\" index=\"2\" host=\"$unique_id$host\"\/>\n"
    nodes+="        <\/node>\n\n"

    echo "Copiando archivos a $host"
    config_content=$(sed -e "s/{registry}/$registry/g" -e "s/{host}/$unique_id$host/g" "$node_template")
    temp_file=$(mktemp)
    echo "$config_content" > "$temp_file"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "mkdir -p $remote_directory"
    sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$temp_file" "swarch@$host:$remote_directory/${node_config_file}"
    rm "$temp_file"
    sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$worker_jar" "swarch@$host:$remote_directory/"
    echo "Ejecutando nodo en $host"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "pkill -f icegridnode"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "cd $remote_directory && mkdir -p data && icegridnode --Ice.Config=${node_config_file} &"
done

for host in "${database_hosts[@]}"; do
    nodes+="        <node name=\"$unique_id$host\">\n"
    nodes+="            <server-instance template=\"Database\" index=\"1\" host=\"$unique_id$host\"\/>\n"
    nodes+="            <server-instance template=\"Database\" index=\"2\" host=\"$unique_id$host\"\/>\n"
    nodes+="        <\/node>\n\n"

    echo "Copiando archivos a $host"
    config_content=$(sed -e "s/{registry}/$registry/g" -e "s/{host}/$unique_id$host/g" "$node_template")
    temp_file=$(mktemp)
    echo "$config_content" > "$temp_file"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "mkdir -p $remote_directory"
    sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$temp_file" "swarch@$host:$remote_directory/${node_config_file}"
    rm "$temp_file"
    sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$database_jar" "swarch@$host:$remote_directory/"
    echo "Ejecutando nodo en $host"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "pkill -f icegridnode"
    echo "asdasdasdasd"
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$host" "cd $remote_directory && mkdir -p data && icegridnode --Ice.Config=${node_config_file}   &"
done

# Usar sed para reemplazar el placeholder con la estructura generada
sed "s/{--hosts--}/$nodes/g" "$application_template" > "$application_file"

# Copiar el archivo de configuración a la máquina remota
echo "Copiando archivos a $registry"
sshpass -p "$password" scp -o StrictHostKeyChecking=no  "$application_file" "swarch@$registry:$remote_directory/application.xml"
rm "$application_file"

sshpass -p "$password" ssh -o StrictHostKeyChecking=no  "swarch@$registry" "cd $remote_directory && icegridadmin --username any --password any --Ice.Config=registry.cfg -e 'application add application.xml'"
sshpass -p "$password" ssh -o StrictHostKeyChecking=no "swarch@$registry" "cd $remote_directory && icegridadmin --username any --password any --Ice.Config=registry.cfg -e 'application update application.xml'"
