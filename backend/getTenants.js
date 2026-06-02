const { Client } = require('pg');

async function getTenants() {
  const client = new Client({
    user: 'postgres',
    host: 'localhost',
    database: 'mall_db',
    password: '1234',
    port: 5432,
  });

  try {
    await client.connect();
    const res = await client.query('SELECT name, domain FROM tenants');
    console.log('--- LISTA DE TIENDAS ---');
    if (res.rows.length === 0) {
      console.log('No hay tiendas registradas aún.');
    } else {
      res.rows.forEach(row => {
        console.log(`Nombre: ${row.name} | Dominio: ${row.domain}`);
      });
    }
  } catch (err) {
    console.error('Error al conectar con la base de datos:', err.message);
  } finally {
    await client.end();
  }
}

getTenants();
