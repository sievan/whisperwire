import { Box, chakra, Text, VStack } from "@chakra-ui/react";
import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import { useParams, useSearchParams } from "react-router";

type Message = {
  id: string;
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
  userIsAuthor: boolean;
};

const MessageContainer = ({
  author,
  content,
  userIsAuthor,
}: MessageContainerProps) => (
  <VStack
    width={"70%"}
    alignItems={"left"}
    gap={1}
    padding={2}
    borderRadius={4}
    backgroundColor={userIsAuthor ? "blue.200" : "gray.200"}
    alignSelf={userIsAuthor ? "flex-end" : "flex-start"}
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

  const [searchParams] = useSearchParams();

  const userName = searchParams.get("userName") || "Default User";

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
      className={className}
    >
      <VStack flexGrow={1} gap={3} paddingX={3}>
        {messages.map(({ id, author, content }) => {
          const userIsAuthor = userName === author;
          return (
            <MessageContainer
              key={id}
              author={author}
              userIsAuthor={userIsAuthor}
              content={content}
            />
          );
        })}
        <div ref={endOfChat} />
      </VStack>
    </Box>
  );
});

export default Messages;
