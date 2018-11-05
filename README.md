# digdag-plugin-mssql

Digdag `mssql>` operator plugin to execute a query on MsSQL server.
Created by converting https://github.com/hiroyuki-sato/digdag-plugin-mysql to mssql version.

**Warning**
* You can't use GO in the sql file. If you need to use that or run complex query, you may call SQLCMD using sh operator like below example.
* sh>: sqlcmd -i test1.sql -f 65001 -S "${host},${port}" -e -b

## configuration

[Release list](https://github.com/kulmam92/digdag-plugin-mssql/releases).

```yaml
_export:
  plugin:
    repositories:
      - file://${repos}
      #- file:///path/to/digdag-plugin-mssql/build/repo
      #- https://jitpack.io
    dependencies:
      - com.github.kulmam92:digdag-plugin-mssql:0.1.1

  mssql:
    host: localhost
    user: sa
    database: tempdb

+step1:
  mssql>: test1.sql

+step2:
  mssql>: test2.sql
  download_file: test.txt  
```

## Run MSSQL container

```
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=mssql_password@1" -p 1433:1433 --name mssql1 -d mcr.microsoft.com/mssql/server:latest-ubuntu
```

## Register mssql password into secrets.

local mode 

```
digdag secrets --local --set mssql.password
```

server mode 

```
digdag secrets --project <project> --set mssql.password
```


## Development

### 1) build

```sh
./gradlew publish
```

Artifacts are build on local repos: `./build/repo`.

### 2) run an example

```sh
digdag selfupdate

rm -rf .digdag/plugin 
digdag run -a --project sample plugin.dig -p repos=`pwd`/build/repo
```
