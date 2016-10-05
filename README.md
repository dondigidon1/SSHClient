# SSHClient
Задать команды можно изменив код в файле.
SSHClient\app\src\main\java\com\example\bob\sshclient\DashboardActivity.java

Там будут методы с именами onScript1Click, onScript2Click, onScript3Click, 
onScript4Click внутри строкой задается команда выполняемая удалено.
Если надо несколько команд их можно разделить через "\n" - перенос строки. 
Например "cd Dir1\nls" - сначала переходим в Dir1 затем выводим список файлов.
