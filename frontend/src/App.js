import React, { useState, useEffect, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function App() {
  const [messages, setMessages] = useState([]);
  const [stompClient, setStompClient] = useState(null);
  const [sender, setSender] = useState('');
  const [receiver, setReceiver] = useState('');
  const [content, setContent] = useState('');

  const loadMessages = useCallback(async () => {
    if (receiver) {
      try {
        const response = await fetch(`http://localhost:8080/api/messages/${receiver}`);
        const data = await response.json();
        setMessages(data);
      } catch (error) {
        console.error('Error loading messages:', error);
      }
    }
  }, [receiver]);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        setStompClient(client);
        client.subscribe('/topic/messages', (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages(prev => [...prev, receivedMessage]);
          // Notifikasi: Jika pesan untuk receiver dan sender bukan diri sendiri
          if (receivedMessage.receiver === receiver && receivedMessage.sender !== sender) {
            if (Notification.permission === 'granted' && document.hidden) {
              new Notification('New Message', { body: `${receivedMessage.sender}: ${receivedMessage.content}` });
            }
            // In-app notification: pesan sudah tampil di list
          }
        });
        loadMessages(); // Load messages saat connect
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      }
    });

    client.activate();

    // Request notification permission
    if (Notification.permission !== 'granted') {
      Notification.requestPermission();
    }

    // Handle online event
    const handleOnline = () => {
      loadMessages();
    };
    window.addEventListener('online', handleOnline);

    return () => {
      if (client) client.deactivate();
      window.removeEventListener('online', handleOnline);
    };
  }, [receiver, sender, loadMessages]); // Include loadMessages

  const sendMessage = () => {
    if (stompClient && sender && receiver && content) {
      const message = { sender, receiver, content };
      stompClient.publish({ destination: '/app/chat', body: JSON.stringify(message) });
      setContent('');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Simple Chat App</h1>
      <div>
        <input type="text" placeholder="Sender" value={sender} onChange={e => setSender(e.target.value)} />
        <input type="text" placeholder="Receiver" value={receiver} onChange={e => setReceiver(e.target.value)} />
      </div>
      <div>
        <input type="text" placeholder="Message" value={content} onChange={e => setContent(e.target.value)} onKeyPress={e => e.key === 'Enter' && sendMessage()} />
        <button onClick={sendMessage}>Send</button>
      </div>
      <div>
        {messages.map((msg, index) => (
          <div key={index}>
            <strong>{msg.sender} to {msg.receiver}:</strong> {msg.content} ({msg.timestamp})
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;