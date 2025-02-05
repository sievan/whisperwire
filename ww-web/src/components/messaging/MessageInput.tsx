import { KeyboardEvent, useState } from "react";
import { Textarea, IconButton, Box, VStack, chakra } from "@chakra-ui/react";
import { FiSend } from "react-icons/fi";
import { useParams } from "react-router";

type PostMessage = {
  author: string;
  content: string;
};

async function sendMessage(conversationId: string, message: PostMessage) {
  await fetch(
    `http://localhost:8080/messages?conversationId=${conversationId}`,
    {
      mode: "cors",
      method: "post",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(message),
    },
  );

  console.log("OK");
}

type MessageInputProps = {
  className?: string;
};

const MessageInput = chakra(({ className }: MessageInputProps) => {
  const { conversationId } = useParams();
  const [message, setMessage] = useState("");

  const handleSubmit = () => {
    if (message.trim()) {
      if (!conversationId) {
        throw new Error("No conversation id provided");
      }
      console.log("Sending message:", message);
      sendMessage(conversationId, {
        author: "Sievan",
        content: message,
      });
      setMessage("");
    }
  };

  const handleKeyPress = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <Box className={className} position="relative" width="full" my={2}>
      <VStack gap={0}>
        <Textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="Type your message..."
          paddingRight={3}
          minH="50px"
          maxH="200px"
          resize="none"
          borderRadius="lg"
          borderColor="gray.400"
          _focus={{
            borderColor: "blue.500",
            boxShadow: "0 0 0 1px var(--chakra-colors-blue-500)",
          }}
        />
        <IconButton
          rounded="full"
          position="absolute"
          bottom="3"
          right="3"
          colorScheme="blue"
          disabled={!message.trim()}
          onClick={handleSubmit}
          aria-label="Send message"
          size="sm"
          _hover={{
            bg: "blue.600",
          }}
        >
          <FiSend />
        </IconButton>
      </VStack>
    </Box>
  );
});

export default MessageInput;
