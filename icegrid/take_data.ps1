$remote_directory = "andres-parra"
$password = "swarch"

# Lista de dispositivos
$client_hosts = @("xhgrid19", "xhgrid20")

# Host key fingerprints
$hostkey = "ssh-ed25519 255 SHA256:tvUus3tmIBfXgIDVZsiNAwd/rzkHWxMdLQxg5wZ+JFE"

# Archivo de salida
$output_file = "merged_out_1.csv"

# Crear o vaciar el archivo de salida
Set-Content -Path $output_file -Value ""

foreach ($client_host in $client_hosts) {
    $remote_file = "$remote_directory/out.csv"
    $local_file = "$client_host-out.csv"

    # Copiar el archivo remoto al local
    Write-Output "Copiando archivo desde $client_host"
    & pscp -batch -pw $password -hostkey $hostkey "swarch@${client_host}:$remote_file" $local_file

    # Verificar si el archivo fue copiado exitosamente
    if (Test-Path $local_file) {
        # Añadir el contenido del archivo copiado al archivo de salida
        Get-Content $local_file | Add-Content -Path $output_file
        # Eliminar el archivo local temporal
        Remove-Item $local_file
    } else {
        Write-Output "Error al copiar el archivo desde $client_host"
    }
}

Write-Output "Archivos combinados en $output_file"