const { Client } = require('pg');

const client = new Client({
  host: 'localhost',
  port: 5432,
  user: 'postgres',
  password: '1234',
  database: 'mall_db',
});

client.connect()
  .then(() => client.query('SELECT * FROM ventas LIMIT 1;'))
  .then(res => {
    console.log(res.rows);
    return client.end();
  })
  .catch(err => {
    console.error(err);
    return client.end();
  });
