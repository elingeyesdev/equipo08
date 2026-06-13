async function testUpload() {
  try {
    console.log('Intentando iniciar sesión...');
    const loginRes = await fetch('http://localhost:3000/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'admin@bolclick.app',
        password: 'SuperAdmin123!'
      })
    });

    const loginData = await loginRes.json();
    if (!loginRes.ok) {
      console.log('Error login:', loginData);
      return;
    }

    const token = loginData.access_token;
    console.log('Token obtenido:', token.substring(0, 10) + '...');

    // Crear un blob de prueba
    const blob = new Blob(['fake webp content'], { type: 'image/webp' });
    const formData = new FormData();
    formData.append('file', blob, 'test.webp');

    console.log('Intentando subir archivo...');
    const uploadRes = await fetch('http://localhost:3000/api/productos/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });

    const uploadData = await uploadRes.json();
    if (uploadRes.ok) {
      console.log('Respuesta exitosa:', uploadData);
    } else {
      console.log('--- ERROR DE SUBIDA ---');
      console.log('Status:', uploadRes.status);
      console.log('Data:', uploadData);
    }
  } catch (err) {
    console.log('Exception:', err.message);
  }
}

testUpload();
