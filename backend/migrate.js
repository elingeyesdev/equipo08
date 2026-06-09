const { Client } = require('pg');

const client = new Client({
  host: 'localhost',
  port: 5432,
  user: 'postgres',
  password: '1234',
  database: 'mall_db',
});

client.connect()
  .then(() => client.query('UPDATE ventas SET "utilidadTotal" = total * 0.4, "costoTotal" = total * 0.6 WHERE "utilidadTotal" = 0 AND total > 0;'))
  .then(res => {
    console.log(`Updated ${res.rowCount} rows`);
    return client.end();
  })
  .catch(err => {
    console.error(err);
    return client.end();
  });
