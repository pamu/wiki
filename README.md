# wiki

FIXME

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 FIXME

Yet Another Wiki in clojure

# DB init

 create table articles (id int auto_increment primary key, title varchar(32), content text, author varchar(32), last_modified_by varchar(32), creation_time timestamp, edits int, modified_time timestamp);

