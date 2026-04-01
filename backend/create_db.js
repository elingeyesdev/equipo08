const { Client } = require('pg');

const client = new Client({
  host: 'localhost',
  port: 5432,
  user: 'postgres',
  password: '1234',
  database: 'postgres'
});

client.connect()
  .then(() => client.query('CREATE DATABASE mall_db'))
  .then(() => console.log('Database created'))
  .catch((err) => console.log('Notice or error: ' + err.message))
  .finally(() => client.end());
