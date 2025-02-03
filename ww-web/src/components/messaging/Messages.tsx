import { Text, VStack } from "@chakra-ui/react";
import { useCallback, useEffect, useRef, useState } from "react";

type Message = {
  author: string;
  content: string;
  conversationId: string;
};

async function fetchMessages(conversationId: string): Promise<Message[]> {
  const res = await fetch(`http://localhost:8080/messages?${conversationId}`, {
    mode: "cors",
    headers: {
      "Content-Type": "application/json",
    },
  });

  return (await res.json()).messages;
}

const Messages = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const ws = useRef<WebSocket | null>(null);

  console.log("render");

  useEffect(() => {
    (async () => {
      const messages = await fetchMessages("TEST");
      setMessages(messages.reverse());
    })();
  }, []);

  const handleOnMessage = useCallback((event: MessageEvent) => {
    console.log("Message: ", event.data);
    setMessages((prevMessages) => [...prevMessages, JSON.parse(event.data)]);
  }, []);

  useEffect(() => {
    ws.current = new WebSocket("ws://localhost:8080/ws?conversationId=2");
    ws.current.onopen = () => console.log("Websocket connection opened");
    ws.current.onclose = () => console.log("Websocket connection closed");

    const wsCurrent = ws.current;

    wsCurrent.onmessage = handleOnMessage;

    return () => {
      wsCurrent.close();
    };
  }, [handleOnMessage]);

  return (
    <>
      {messages.map(({ author, content }) => (
        <VStack width={"100%"} alignItems={"left"} gap={1} paddingY={2}>
          <Text>Author: {author}</Text>
          <Text>{content}</Text>
        </VStack>
      ))}
    </>
  );
};

export default Messages;
