# ДЗ Thread Pool
## Описание проекта

## Стек

* Java 21
* Maven
* SLF4J API

## Установка и запуск
Склонировать к себе на устройство, запустить через IDE.

## Тестирование

Запуск `App.java` демонстрирует базовую работу пула с 500 задачами, по умолчанию с параметрами:

```
corePoolSize = 20
maxPoolSize  = 40
queueSize    = 25
keepAliveTime= 5 секунд
minSpareThreads = 4
```

В консоли выводятся логи о приёме и выполнении задач, а также итоговая статистика: число выполненных/отклонённых задач и среднее время на задачу.

Результаты выполнения<br/>
Общее время: 1 мс<br/>
Выполнено задач: 500<br/>
Отклонено задач: 0<br/>
Среднее время на задачу: 0.1 мс<br/>

## Отчёт

### Анализ производительности

Тест на процессоре i7-14700k (20 ядер, 28 потоков) с параметрами, указанными выше

Тест с очередью размера 1, 100 задач.
#### 

| Метрика                      | MultiQueueExecutor | 
| ---------------------------- | ------------------ | 
| Общее время выполнения (мс)  |       9 мс         |
| Выполнено задач              |       40           |
| Отклонено задач              |       60           | 
| Среднее время на задачу (мс) |       0.23 мс      |

Видно, что был достигнут maxPoolSize и ровно столько задач было выполнено, все остальные были отброшены

Тест с очередью размера 5, 100 задач.
#### 

| Метрика                      | MultiQueueExecutor | 
| ---------------------------- | ------------------ | 
| Общее время выполнения (мс)  |       1 мс         |
| Выполнено задач              |       100          |
| Отклонено задач              |        0           | 
| Среднее время на задачу (мс) |       0.01 мс      |

Видно, что был достигнут maxPoolSize и ровно столько задач было выполнено, все остальные были отброшены.

### Мини исследование
При нахождении баланса между размером очереди и corePoolSize можно достигнуть оптимальных настроек, которые однако зависят от мощности сервера.<br/>
В разделе тестирование был представлен оптимальный вариант для 500 задач.

### Принцип работы механизма распределения задач
1. Round-Robin: очереди задач выбираются циклически.
2. Автоматическое масштабирование: если очередь заполнена и кол-во потоков меньше лимита, то создаётся новый воркер.
3. Завершение простаивающих: воркер завершается, если стоит без задач дольше keepAliveTime.
4. Поддержание резерва: при числе простаивающих меньше minSpareThreads создаются дополнительные потоки.
