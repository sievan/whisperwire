import { Box, chakra, Text, VStack } from "@chakra-ui/react";
import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import { useParams } from "react-router";

type Message = {
  author: string;
  content: string;
  conversationId: string;
};

async function fetchMessages(conversationId?: string): Promise<Message[]> {
  const res = await fetch(
    `http://localhost:8080/messages?conversationId=${conversationId}`,
    {
      mode: "cors",
      headers: {
        "Content-Type": "application/json",
      },
    },
  );

  return (await res.json()).messages;
}

type MessageContainerProps = {
  author: string;
  content: string;
};

const MESSAGE_SPACING = 3;

const MessageContainer = ({ author, content }: MessageContainerProps) => (
  <VStack
    width={"70%"}
    alignItems={"left"}
    gap={1}
    padding={2}
    margin={MESSAGE_SPACING}
    borderRadius={4}
    backgroundColor={"blue.100"}
  >
    <Text color="bodyText" fontSize="sm" fontWeight="bold">
      {author}
    </Text>
    <Text>{content}</Text>
  </VStack>
);

type MessagesProps = {
  className?: string;
};

const Messages = chakra(({ className }: MessagesProps) => {
  const { conversationId } = useParams();
  const [messages, setMessages] = useState<Message[]>([]);
  const ws = useRef<WebSocket | null>(null);
  const endOfChat = useRef<HTMLDivElement>(null);

  useEffect(() => {
    (async () => {
      const messages = await fetchMessages(conversationId);
      setMessages(messages.reverse());
    })();
  }, [conversationId]);

  const handleOnMessage = useCallback((event: MessageEvent) => {
    console.log("Message: ", event.data);
    setMessages((prevMessages) => [...prevMessages, JSON.parse(event.data)]);
  }, []);

  useEffect(() => {
    ws.current = new WebSocket(
      `ws://localhost:8080/ws?conversationId=${conversationId}`,
    );
    ws.current.onopen = () => console.log("Websocket connection opened");
    ws.current.onclose = () => console.log("Websocket connection closed");

    const wsCurrent = ws.current;

    wsCurrent.onmessage = handleOnMessage;

    return () => {
      wsCurrent.close();
    };
  }, [handleOnMessage, conversationId]);

  useLayoutEffect(() => {
    if (!endOfChat.current) return;

    endOfChat.current.scrollIntoView();
  });

  return (
    <Box
      overflowY="scroll"
      border="1px solid"
      borderRadius={8}
      borderColor="gray.300"
      display="flex"
      alignItems="flex-end"
      className={className}
    >
      <Box flexGrow={1}>
        {messages.map(({ author, content }) => (
          <MessageContainer author={author} content={content} />
        ))}
        <div ref={endOfChat} />
      </Box>
    </Box>
  );
});

export default Messages;
