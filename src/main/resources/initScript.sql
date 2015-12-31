/* for sqlite3*/
/* 用户表 */
create table user (
  id integer primary key autoincrement,
  account varchar(20) not null unique,
  name varchar(20) not null,
  password varchar(100) not null,
  salt varchar(20) not null,
  id_card_no int not null,
  enabled int not null
  /*身份证号*/
);

create index user_id_card_no on user (id_card_no);
create index user_name on user(name);

/* 部门表 */
create table department(
  id integer primary key autoincrement,
  name varchar(20) not null,
  parent_id integer REFERENCES id ,
  is_root boolean not null
);

create index department_name on department(name);

create table department_user (
  department_id integer REFERENCES department(id),
  user_id integer REFERENCES user(id)
);

/* 职位 */
create table job(
  id integer PRIMARY KEY AUTOINCREMENT ,
  name varchar(20) not null,
  department_id integer REFERENCES department(id)
);

create index job_name on job(name);

create table user_job(
  user_id integer REFERENCES user(id),
  job_id integer REFERENCES job(id)
);

/* 权限 */
create table privilege (
  id integer PRIMARY KEY AUTOINCREMENT ,
  name varchar(20) not null,
  process_name varchar(100) not null,
  task_name varchar(100) not null,
  note varchar(255) not null
);

create index privilege_name on privilege(name);

create index privilege_process_task on privilege(process_name,task_name);

create table department_privilege (
  department_id integer REFERENCES department(id),
  privilege_id integer REFERENCES privilege(id)
);

create table job_privilege(
  job_id integer REFERENCES job(id),
  privilege_id integer REFERENCES privilege(id)
);

insert into department(name,is_root) values("林口林业局",1);




