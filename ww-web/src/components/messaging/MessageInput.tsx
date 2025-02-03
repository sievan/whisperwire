import { KeyboardEvent, useState } from "react";
import { Textarea, IconButton, Box, VStack } from "@chakra-ui/react";
import { FiSend } from "react-icons/fi";

type PostMessage = {
  author: string;
  content: string;
};

async function sendMessage(conversationId: string, message: PostMessage) {
  const res = await fetch(
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
  // return (await res.json());
}

const MessageInput = () => {
  const [message, setMessage] = useState("");

  const handleSubmit = () => {
    if (message.trim()) {
      console.log("Sending message:", message);
      sendMessage("2", {
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
    <Box position="relative" width="full" maxW="2xl" mx="auto">
      <VStack gap={0}>
        <Textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="Type your message..."
          pr="14"
          minH="50px"
          maxH="200px"
          resize="vertical"
          borderRadius="lg"
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
};

export default MessageInput;
