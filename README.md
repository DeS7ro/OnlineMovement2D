Move around in a room with WASD. Communication occurs via sockets, specifically multiclient with a server that, upon a new connection with a client, creates a thread to handle this client.
When the client moves, its position is sent to the server, which then broadcasts the position to all other connected clients.
