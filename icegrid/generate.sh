#!/bin/bash

# Nombre del archivo de salida
output_file="cedulas.txt"

# Número de cédulas a generar
num_cedulas=1000000

# Rango de números de cédula
min_cedula=100000000
max_cedula=1000000000

# Generar números de cédula aleatorios y escribirlos en el archivo
for ((i=0; i<num_cedulas; i++)); do
    echo $((RANDOM % (max_cedula - min_cedula + 1) + min_cedula)) >> $output_file
done

echo "Archivo $output_file generado con $num_cedulas cédulas aleatorias."