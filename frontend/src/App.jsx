import { useState, useEffect } from 'react'

function App() {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch('/api/home')
            .then(res => res.json())
            .then(data => setData(data))
            .catch(err => setError(err.message));
    }, []);

    return (
        <div style={{ padding: '20px' }}>
            <h1>Проверка связи с Backend</h1>
            {error && <p style={{color: 'red'}}>Ошибка: {error}</p>}
            {data && <pre>{JSON.stringify(data, null, 2)}</pre>}
        </div>
    )
}

export default App