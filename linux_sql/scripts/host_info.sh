#! /bin/bash

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters provided"
  exit 1
fi

lscpu_out=$(lscpu)
cpuinfo_out=$(cat /proc/cpuinfo)
meminfo_out=$(cat /proc/meminfo)
hostname=$(hostname -f)

cpu_number=$(echo "$lscpu_out"  | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out"  | egrep "^Architecture" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"  | egrep "^Model" | awk '{print $3,$4,$5,$6,$7}' | xargs)
cpu_mhz=$(echo "$cpuinfo_out" | egrep "^cpu MHz" | tail -1 | awk '{print $4}' | xargs)
l2_cache=$(echo "$lscpu_out"  | egrep "^L2 cache" | awk '{print $3}' | xargs)
total_mem=$(echo "$meminfo_out" | egrep "^MemTotal" | awk '{print$2}' | xargs)
timestamp=$(date +"%Y-%m-%d %T")

insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem) VALUES ('$hostname', '$cpu_number', '$cpu_architecture', '$cpu_model', '$cpu_mhz', '$l2_cache', '$timestamp', '$total_mem')"

export PGPASSWORD=$psql_password

psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"

exit $?