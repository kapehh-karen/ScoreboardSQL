ScoreboardSQL
=============

Использует PluginManager, Vault

Конфиг:
<pre>
connect:
  ip: <host> # хост с БД
  db: <name> # название базы
  login: <login> # логин юзера
  password: <passwd> # его пароль
timer:
  ticks: <ticks> # количество тиков для обновления инфы (1000 это 1 минута, примерно)
</pre>

Команды:
<ul>
<li><code>/scoreboard toggle</code> - для игроков, вкл/выкл показ панельки</li>
<li><code>/scoreboard reload</code> - для консоли, заново загрузить инфу с конфига, например чтоб изменить инфу о соединения БД или количество тиков</li>
</ul>
