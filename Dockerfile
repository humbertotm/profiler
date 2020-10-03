FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
EXPOSE 36096/tcp
CMD ["lein", "repl", ":headless", ":host", "0.0.0.0", ":port", "36096"]
