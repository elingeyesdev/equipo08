const { Client } = require('pg');
const client = new Client({
  user: 'postgres',
  password: '1234',
  host: 'localhost',
  port: 5432,
  database: 'mall_db'
});

client.connect()
  .then(() => client.query('SELECT id, name, "imagen_url" FROM productos LIMIT 20'))
  .then(res => {
    console.table(res.rows);
    return client.end();
  })
  .catch(err => {
    console.error(err);
    process.exit(1);
  });
