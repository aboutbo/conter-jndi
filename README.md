# conter-jndi
获取jndi注入的远程恶意类地址，应急溯源用  
最近log4j应急，日志排查发现太多log4j扫描记录，但无法确定具体是哪条日志触发，漏洞执行的命令和jndi注入又对不上，要是能获取到jndi注入的恶意类文件就方便排查了，所以就写了这么个小工具。  

### Usage
```
java -jar conter-jndi-1.0-SNAPSHOT.jar -u "ldap://127.0.0.1:9999/Calc"
output:  
http://127.0.0.1/Calc.class
```  
