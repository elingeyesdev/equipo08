const baseURL = 'http://localhost:3000/api';

async function test() {
  try {
    const res = await fetch(`${baseURL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'admin', password: '123' })
    });
    const authData = await res.json();
    const token = authData.access_token;
    
    if (!token) {
      console.log('No token:', authData);
      return;
    }

    const ventasRes = await fetch(`${baseURL}/ventas`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await ventasRes.json();
    console.log(JSON.stringify(data[0], null, 2));
  } catch(e) {
    console.error(e.message);
  }
}
test();
