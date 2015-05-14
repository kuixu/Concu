# Concu
Concu   : Concurrent to run optionally numbers of commamdline.


## Download Excutable file 

https://github.com/barrykui/Concu/blob/master/bin/concu.jar

## Manual
*Concu* is easy to use.

``` shell

$ java -jar concu.jar   

Concu   : Concurrent to run optionally numbers of commamdline.
Version : 1.0.1
Usage   : 
 -f  <str>    Commandline list file.
 -t  <int>    Number of concurrent threads [1, 50]; 
 -w  <str>    Daemon name to be watched. eg.'java','wget'.
 -v           View output.
```

### Example 
Create commandline list file formatted as *wgettask.list* below. Concurrently run 5 task with [`Concu`](https://github.com/barrykui/Concu.git), until all tasks are completed.

``` shell
$ cat wgettask.list
wget http:/xxx.xxx.xxxx0.tar.gz
wget http:/xxx.xxx.xxxx1.tar.gz
wget http:/xxx.xxx.xxxx2.tar.gz
wget http:/xxx.xxx.xxxx3.tar.gz
wget http:/xxx.xxx.xxxx4.tar.gz
wget http:/xxx.xxx.xxxx5.tar.gz
wget http:/xxx.xxx.xxxx6.tar.gz
wget http:/xxx.xxx.xxxx7.tar.gz
wget http:/xxx.xxx.xxxx8.tar.gz
wget http:/xxx.xxx.xxxx9.tar.gz


$ java -jar concu.jar -f wgettask.list -t 5 -w wget

```


