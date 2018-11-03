IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 't1')begin
    create table t1(col1 int);
end

insert into t1 values(10);