# digdag-plugin-mssql

Digdag `mssql>` operator plugin to execute a query on MsSQL server.

**Caution**: This isnot working yet.

## configuration

[Release list](https://github.com/kulmam92/digdag-plugin-mssql/releases).

```yaml
_export:
  plugin:
    repositories:
    repositories:
      #- file://${repos}
      #- file:///path/to/digdag-plugin-mssql/build/repo
      - https://jitpack.io
    dependencies:
      - com.github.kulmam92:digdag-plugin-mssql:0.1.1

  mssql:
    host: localhost
    user: sa
    database: digdag_test
    ssl: true

+step1:
  mssql>: test.sql
  download_file: test.txt
```

Register mssql password into secrets.

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

rm -rf sample/.digdag/plugin 
digdag run -a --project sample plugin.dig -p repos=`pwd`/build/repo
```
