编码规范
业务Mapper 需要继承于BaseMapper
业务BS 	     需要继承于BaseBS 并实现getMapper()方法
业务Service层不可直接访问Mapper 必须通过BS层
每个BS层只可访问自己的Mapper 如有需要 请将其他Mapper所属的BS层注入当前BS
BaseBS层方法说明

protected abstract BaseMapper<T> getMapper();
需要子类实现 返回具体的BaseMapper子类

public List<T> selectList(Map<String,Object> map,Class<T> entityClazz,Order...orders);
根据条件查询列表,参数说明 map:参数键值对 key为所查询的条件   value为条件的具体值
查询条件由查询列和符号组成 查询列对应实体类中被JdbcColumn注解的字段名 符号为 > < = <> 等运算符号
查询列和符号之间需要空格 例如:
如果查询的是表A 中 name 包含'abc'的数据 
实体如下
@MybatisEntity(tableName="A")
public class A {
	....
	@JdbcColumn(columnName="NAME")
	private String name;
	@JdbcColumn(columnName="CREATE_TIME",defaultValue="now()",updateIgnore=true)
	private Timestamp createTime;
	....
}

查询时调用方式如下
Map<String,Object> map = new HashMap<String,Object>();
map.put("name like","%abc%");
entityBs.selectList(map,A.class,new Order("createTime",false));
如果只有查询列 没有查询符号 则符号为'=' 例如
Map<String,Object> map = new HashMap<String,Object>();
map.put("name","abc");
entityBs.selectList(map,A.class,new Order("createTime",false));
表示查询 name = abc的A表数据 并按照 CREATE_TIME字段进行降序排序


Order为排序实体 构造Order传入两个参数 排序列(实体中的属性名) 是否是升序 true 升序 false 降序

public T selectOneCondition(Map<String,Object> map,Class<T> entityClazz);
根据条件查询单条数据 使用方式与selectList方法类似

public void batchInsert(List<T> list,Class<T> clz);
批量插入实体对象

public void save(T t);
保存实体 若t的主键值为空是新增,t的主键值非空则是修改

public void update(T t);
修改实体

public void insert(T t);
新增单个实体对象

public void deleteById(Serializable id,Class<T> entityClazz);
根据id删除实体对象

public void deleteByCondition(Map<String,Object> argMap,Class<T> entityClazz);
根据条件删除实体对象

注解说明
EntityParent
当有几个实体 继承同一个父类,且该父类拥有数据库的字段的时候 需要用该注解标注

MybatisEntity
用来标注实体类
属性:
tableName 实体对应的表的名称

JdbcColumn
标注实体的字段
属性:
	columnName 字段对应数据库表的列名
	defaultValue 当实体字段值为空时 数据库提供的默认值 可以填写数据库的函数
	例如
	@JdbcColumn(columnName="CREATE_TIME",defaultValue="now()",updateIgnore=true)
	private Timestamp createTime;
	表示如果createTime值为空 则调用数据库的now()方法的结果填充该值 
	
	同时defaultValue调用的数据库方法可以传入参数,例:
	@JdbcColumn(columnName="sort_num",defaultValue="getsort(#{name})")
	private Integer sortNum;
	表示如果sortNum为空 则调用数据库方法getsort()生成,而参数为该实体字段name的值
	insertIgnore
	表示新增时忽略,即 在生成insert语句时会忽略该字段
	updateIgnore
	表示更新时忽略,即 在进行update操作时不理会该字段
	
	isPrimaryKey
	是否为主键,当设置为true时表示该字段为实体主键
	
