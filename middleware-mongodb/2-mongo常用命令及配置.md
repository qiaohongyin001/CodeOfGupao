## 2 MongoDB的常用命令及配置

### 2.1 安装 MongoDB 数据库(Windows 和 Linux 环境) 

打开官网：https://www.mongodb.com/download-center?jmp=nav#community
选择 Community Server 4.0.1 的版本。

- 下载安装包
  - wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-amazon2-4.0.1.tgz

- 解压安装包
  - tar -zxvf mongodb-linux-x86_64-amazon2-4.0.1.tgz 

- 在mongodb目录下创建db与logs文件夹
  - mkdir /data/logs
  - mkdir -p /data/db

- 在mongodb/bin下新建配置

~~~java
vi mongodb.conf
dbpath = /usr/local/mongodb/data/db #数据文件存放目录
logpath = /usr/local/mongodb/logs/mongodb.log #日志文件存放目录
port = 27017  #端口
fork = true  #以守护程序的方式启用，即在后台运行
auth=true
bind_ip=0.0.0.0
~~~

- 环境变量配置

~~~java
vi /etc/profile 
export MONGODB_HOME=/usr/local/mongodb
export PATH=$PATH:$MONGODB_HOME/bin
保存后，重启系统配置
source /etc/profile
~~~

- 启动

~~~java
在/mongodb/bin下
mongod -f mongodb.conf 或 ./mongod -f mongodb.conf

启动过程中会报错，报错内容：/lib64/libc.so.6: version `GLIBC_2.18' not found (required by /lib64/libstdc++.so.6)
解决方法：
    curl -O http://ftp.gnu.org/gnu/glibc/glibc-2.18.tar.gz
    tar zxf glibc-2.18.tar.gz 
    cd glibc-2.18/
    mkdir build
    cd build/
    ../configure --prefix=/usr
    make -j2
    make install
~~~

- 关闭

~~~java
mongod -f ./mongodb.conf --shutdown  或./mongod -f ./mongodb.conf --shutdown
~~~

- 开启端口

~~~java
firewall-cmd --zone=public --add-port=27017/tcp --permanent
查看端口
firewall-cmd --permanent --query-port=27017/tcp
重启防火墙
firewall-cmd --reload
~~~

- 连接mongodb数据库

~~~java
./mongo
~~~

- 设置mongodb.service启动服务，设置开机启动

~~~java
cd /lib/systemd/system  
sudo vi mongodb.service 

编辑其内容为：（linux新版本才有简单方式自定义服务）
[Unit]  
Description=mongodb  
After=network.target remote-fs.target nss-lookup.target  
[Service]  
Type=forking  
ExecStart=/usr/local/mongodb/bin/mongod --config /usr/local/mongodb/bin/mongodb.conf  
ExecReload=/bin/kill -s HUP $MAINPID  
ExecStop=/usr/local/mongodb/bin/mongod --shutdown --config /usr/local/mongodb/bin/mongodb.conf  
PrivateTmp=true  
[Install]  
WantedBy=multi-user.target
~~~

- 设置mongodb.service权限

~~~java
chmod +x mongodb.service
chown root:root mongodb.service
~~~

- 系统mongodb.service的操作命令

~~~java
#启动服务  
systemctl start mongodb.service  
#关闭服务  
systemctl stop mongodb.service  
#开机启动  
systemctl enable mongodb.service 
~~~

- 创建用户

~~~java
创建用户管理员：
   use admin
   db.createUser({user:"root",pwd:"root123456",roles:["userAdminAnyDatabase"]})
   db.auth('root','root123456')
以用户管理员身份登录，并切换数据库，创建数据库用户：
   切换到test数据库
   use test
   创建用户名、密码、角色
   db.createUser({user:"username",pwd:"@user123456*",roles:[{role:"readWrite",db:"securitydata"}]})
   设置mongodb配置中的auth为true（/etc/mongod.conf）：
   security:
     authorization: enabled
   验证mongodb数据库权限。
   db.auth('user','@user123456*')
         
         
> use admin
switched to db admin
> db.createRole({role:'sysadmin',roles:[],privileges:[{resource:{anyResource:true},actions:['anyAction']}]})

> use woplus
switched to db woplus
> db.createUser({user:'huhao',pwd:'aaa',roles:[{role:'sysadmin',db:'admin'}]})
用户进行验证，就可以正常插入数据了
~~~

### 2.2 MongoDB常用命令

#### 2.2.1 创建数据库

~~~mongodb
use testdb
~~~

#### 2.2.2 创建集合

~~~mongodb
> db.t_member.insert({name:"zhaomin",age:23})
WriteResult({ "nInserted" : 1 })
~~~

#### 2.2.3 查询

~~~mongodb
> db.t_member.find()
{ "_id" : ObjectId("5b99f6d353ea957d22d304aa"), "name" : "zhaomin", "age" : 23 }
> db.t_member.findOne()
{
        "_id" : ObjectId("5b99f6d353ea957d22d304aa"),
        "name" : "zhaomin",
        "age" : 23
}
~~~

#### 2.2.4 修改

~~~mongodb
#不会影响其他属性列 ，主键冲突会报错
> db.t_member.update({name:'zhaomin'},{$set:{age:8888}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
#第三个参数为 true 则执行 insertOrUpdate 操作，查询出则更新，没查出则插入， 或者
 db.t_member.update({name:'huhao'},{$set:{age:8888}},true)
WriteResult({
        "nMatched" : 0,
        "nUpserted" : 1,
        "nModified" : 0,
        "_id" : ObjectId("5b99f99dbcac2adf05199c7c")
})
~~~

#### 2.2.5 删除

~~~mongodb
#删除满足条件的第一条 只删除数据 不删除索引
> db.t_member.remove({name:'zhaomin'})
WriteResult({ "nRemoved" : 1 })
#删除集合
db.t_member.drop();
#删除数据库
db.dropDatabase();
~~~

#### 2.2.6 查看集合 

~~~mongodb
> show collections
t_member
~~~

#### 2.2.7 查看数据库

~~~mongodb
> show dbs
admin   0.000GB
config  0.000GB
local   0.000GB
test    0.000GB
~~~

#### 2.2.8 插入数据

~~~mongo
#不允许键值重复
> db.t_member.insert({name:'zhaofengwei',age:29})
WriteResult({ "nInserted" : 1 })
#若键值重复，可改为插入操作
> db.t_member.save({name:'zhaofengwei',age:29})
WriteResult({ "nInserted" : 1 })
~~~

#### 2.2.9 批量更新

~~~mongodb
#批量操作需要和选择器同时使用，第一个 false 表示不执行 insertOrUpdate 操作，第二个 true 表示 执行批量
> db.t_member.update({name:"zhaofengwei"},{$set:{name:"xiaohetao"}},false,true);
WriteResult({ "nMatched" : 3, "nUpserted" : 0, "nModified" : 3 })
~~~

#### 2.2.10 更新器使用$set : 指定一个键值对，若存在就进行修改，不存在则添加

~~~mongodb
$inc :只使用于数字类型，可以为指定键值对的数字类型进行加减操作:
> db.t_member.update({name:"huhao"},{$inc:{age:2}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
执行结果是名字叫“zhangsan”的年龄加了 2

$unset : 删除指定的键
> db.t_member.update({name:"huhao"},{$unset:{age:1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

$push : 数组键操作:1、如果存在指定的数组，则为其添加值;2、如果不存在指定的数组，则 创建数组键，并添加值;3、如果指定的键不为数组类型，则报错;

$addToSet : 当指定的数组中有这个值时，不插入，反之插入
#则不会添加到数组里
> db.t_member.update({name:"huhao"},{$addToSet:{classes:"English"}});
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

$pop:删除指定数组的值，当 value=1 删除最后一个值，当 value=-1 删除第一个值
#删除了最后一个值
> db.t_member.update({name:"huhao"},{$pop:{classes:1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

$pull : 删除指定数组指定的值
#$pullAll 批量删除指定数组 
> db.t_member.update({name:"huhao"},{$pull:{classes:"English"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
#若数组中有多个 Chinese，则全删除 
db.t_member.update({name:"huhao"},{$pull:{classes:["Chinese"]}})

$ : 修改指定数组时，若数组有多个对象，但只想修改其中一些，则需要定位器:
db.t_member.update({"classes.type":"AA"},{$set:{"classes.$.sex":"male "}})

$addToSet 与 $each 结合完成批量数组更新操作
db.t_member.update({name:"zhangsan"},{$set:{classes:{$each:["chinese" ,"art"]}}})

runCommand 函数和 findAndModify 函数
runCommand({
    findAndModify:"persons",
    query:{查询器},
	sort:{排序}, 
	update:{修改器},
	new:true 是否返回修改后的数据
});
runCommand 函数可执行 mongdb 中的特殊函数
findAndModify 就是特殊函数之一，用于返回执行返回 update 或 remove 后的文档 例如:
db.runCommand({
    findAndModify:"persons",
    query:{name:"zhangsan"},
    update:{$set:{name:"lisi"}},
    new:true
})
~~~

### 2.3 高级查询详解

~~~mongodb
db.persons.find({条件},{指定键});
第一个空括号表示查询全部数据，
第二个括号中值为 0 表示不返回，值为 1 表示返回，

默认情况下若不指定主键，主键总是会被返回;

> db.t_member.find({},{_id:0,name:1})
{ "name" : "huhao" }
{ "name" : "xiaohetao" }
{ "name" : "xiaohetao" }
{ "name" : "xiaohetao" }
{ "name" : "aaa" }
{ "name" : "bbb" }
~~~

#### 2.3.1 查询条件

~~~mongodb
比较操作符:$lt: < $lte: <= $gt: > $gte: >= $ne: !=
#查询年龄大于等于 25 小于等于 30 的人

> db.t_member.find({age:{$gte:25,$lte:30}},{_id:0,name:1,age:1})
{ "name" : "xiaohetao", "age" : 29 }
{ "name" : "xiaohetao", "age" : 29 }
{ "name" : "xiaohetao", "age" : 29 }

#查询出所有名字不是huhao的人的信息
> db.t_member.find({name:{$ne:"huhao"}},{_id:0,name:1})
{ "name" : "xiaohetao" }
{ "name" : "xiaohetao" }
{ "name" : "xiaohetao" }
{ "name" : "aaa" }
{ "name" : "bbb" }
~~~

#### 2.3.2 包含与不包含（仅针对数组）

~~~mongodb
$in 或 $nin
#查询国籍是中国或美国的学生信息
> db.student.find({country:{$in:["China","USA"]}},{_id:0,name:1,country:1})
{ "name" : "caopeipei", "country" : "China" }
{ "name" : "huhao", "country" : "China" }
{ "name" : "aobama", "country" : "USA" }
~~~

#### 2.3.3 $or 查询

~~~mongodb
#查询语文成绩大于 85 或者英语大于 90 的学生信息 
db.t_member.find({$or:[{c:{$gt:85}},{e:{$gt:90}}]},{_id:0,name:1,c:1,e:1})

#把中国国籍的学生上增加新的键 
> db.student.update({country:"China"},{$set:{score:100}},false,true)
WriteResult({ "nMatched" : 2, "nUpserted" : 0, "nModified" : 2 })

#查询出 sex 为 null 的人 
db.t_member.find({sex:{$in:[null]}},{_id:0,name:1,sex:1})
~~~

#### 2.3.4 正则表达式

~~~mongodb
#查询出名字中存在”li”的学生的信息 
> db.student.find({name:/hu/i},{_id:0,name:1})
{ "name" : "huhao" }
~~~

#### 2.3.5 $not 的使用 

~~~mongodb
$not 和$nin 的区别：$not可以用在任何地方儿 $nin是用到集合上的
#查询出名字中不存在”li”的学生的信息 
db.t_member.find({name:{$not:/li/i}},{_id:0,name:1})
~~~

#### 2.3.6 $all 与 index 的使用

~~~mongodb
#查询喜欢看 MONGOD 和 JS 的学生 
db.t_member.find({books:{$all:["JS","MONGODB"]}},{_id:0,name:1}) 
#查询第二本书是 JAVA 的学习信息
db.t_member.find({"books.1":"JAVA"},{_id:0,name:1,books:1})
~~~

#### 2.3.7 $size 的使用，不能与比较查询符同时使用

~~~mongodb
#查询出喜欢的书籍数量是 4 本的学生
db.t_member.find({books:{$size:4}},{_id:0,name:1})
~~~

#### 2.3.8 查询出喜欢的书籍数量大于 4 本的学生本的学生

~~~mongodb
#增加 size 键
db.t_member.update({},{$set:{size:4}},false,true)
#添加书籍,同时更新 size
db.t_member.update({name:"jim"},{$push:{books:"ORACL"},$inc:{size:1} })
#查询大于 3 本的
db.t_member.find({size:{$gt:4}},{_id:0,name:1,size:1})
~~~

#### 2.3.9 $slice 操作符返回文档中指定数组的内部值

~~~mongodb
#查询出 Jim 书架中第 2~4 本书 
db.t_member.find({name:"jim"},{_id:0,name:1,books:{$slice:[1,3]}})
#查询出最后一本书 
db.t_member.find({name:"jim"},{_id:0,name:1,books:{$slice:-1}})
~~~

#### 2.3.10 文档查询 

~~~mongodb
查询出在 K 上过学且成绩为 A 的学生

#绝对查询，顺序和键个数要完全符合
db.t_member.find({school:{school:"K","score":"A"}},{_id:0,name:1})
#对象方式,但是会出错，多个条件可能会去多个对象查询
db.t_member.find({"school.school":"K","school.score":"A"},{_id:0,nam e:1})
正确做法单条条件组查询$elemMatch
db.t_member.find({school:{$elemMatch:{school:"K",score:"A"}},{_id:0,n ame:1}) db.t_member.find({age:{$gt:22},books:"C++",school:"K"},{_id:0,name:1,age:1,books:1,school:1})
~~~

#### 2.3.11 分页与排序 

~~~mongodb
#limit 返回指定条数 查询出 persons 文档中前 5 条数据
db.t_member.find({},{_id:0,name:1}).limit(5)
#指定数据跨度 查询出 persons 文档中第 3 条数据后的 5 条数据
db.t_member.find({},{_id:0,name:1}).limit(5).skip(3)
#sort 排序 1 为正序，-1 为倒序
db.t_member.find({},{_id:0,name:1,age:1}).limit(5).skip(3).sort({age: 1})

注意：
	mongodb 的 key 可以存不同类型的数据排序就也有优先级 
	最小值->null->数字->字符串->对象/文档->数组->二进制->对象 ID->布尔->日期->时间戳->正则 ->最大值
~~~

#### 2.3.11 游标

~~~mongodb
#利用游标遍历查询数据
var persons = db.persons.find();
    while(persons.hasNext()){
    obj = persons.next();
    print(obj.name)
}

游标几个销毁条件
1).客户端发来信息叫他销毁
2).游标迭代完毕
3).默认游标超过 10 分钟没用也会别清除 
~~~

#### 2.3.13 查询快照 

~~~mongodb
#快照后就会针对不变的集合进行游标运动了,看看使用方法.

#用快照则需要用高级查询
db.persons.find({$query:{name:”Jim”},$snapshot:true})


~~~

#### 2.3.14 高级查询选项

~~~mongodb
1)$query
2)$orderby
3)$maxsan:integer 最多扫描的文档数 4)$min:doc 查询开始
5)$max:doc 查询结束
6)$hint:doc 使用哪个索引 
7)$explain:boolean 统计 
8)$snapshot:boolean 一致快照

#查询点(70,180)最近的 3 个点
db.map.find({gis:{$near:[70,180]}},{_id:0,gis:1}).limit(3)

#查询以点(50,50)和点(190,190)为对角线的正方形中的所有的点
db.map.find({gis:{$within:{$box:[[50,50],[190,190]]}}},{_id:0,gis:1})

#查询出以圆心为(56,80)半径为 50 规则下的圆心面积中的点
db.map.find({gis:{$with:{$center:[[56,80],50]}}},{_id:0,gis:1})
~~~

#### 2.3.15 Count+Distinct+Group

~~~mongodb
#count 查询结果条数
db.persons.find({country:"USA"}).count()

#Distinct 去重
请查询出 persons 中一共有多少个国家分别是什么
#key 表示去重的键 
db.runCommand({distinct:"persons",key:"country"}).values

#group 分组
db.runCommand({ 
	group:{ 
		ns:"集合的名字", 
		key:"分组键对象", 
		initial:"初始化累加器", 
		$reduce:"分解器", 
		condition:"条件", 
		finalize:"组完成器"
}})
分组首先会按照 key 进行分组,每组的 每一个文档全要执行$reduce 的方法,他接收 2 个参数一个是组 内本条记录,一个是累加器数据.

请查出 persons 中每个国家学生数学成绩最好的学生信息(必须在 90 以上)
db.runCommand({
    group:{
        ns:"persons",
        key:{"country":true},
        initial:{m:0},
        $reduce:function(doc,prev){
           if(doc.m>prev.m){
               prev.m = doc.m;
               prev.name = doc.m;
               prev.country = doc.country;
            } 
        },
        condition:{m:{$gt:90}},
        finalize:function(prev){
            prev.m = prev.name+" comes from "+prev.country+" ,Math score is "+prev.m;
        } 
	}
})

#函数格式化分组键
#如果集合中出现键 Counrty 和 counTry 同时存在
$keyf:function(doc){
    if(doc.country){
       return {country:doc.country}
    }
    return {country:doc.counTry}
}
~~~

#### 2.3.16 常用命令举例

~~~mongodb
#查询服务器版本号和主机操作系统
db.runCommand({buildInfo:1})
#查询执行集合的详细信息,大小,空间,索引等
db.runCommand({collStats:"persons"})
#查看操作本集合最后一次错误信息
db.runCommand({getLastError:"persons"})


~~~

#### 2.3.17 固定集合

特性：固定集合默认是没有索引的就算是_id 也是没有索引的，由于不需分配新的空间他的插入速度是非常快的 固定集合的顺序确定的导致查询速度是非常快的，最适合就是日志管理。

~~~mongodb
创建固定集合
#创建一个新的固定集合要求大小是 100 个字节,可以存储文档 10 个
db.createCollection("mycoll",{size:100,capped:true,max:10})
#把一个普通集合转换成固定集合
db.runCommand({convertToCapped:"persons",size:1000})
#对固定集合反向排序，默认情况是插入的顺序排序
db.mycoll.find().sort({$natural:-1})
~~~