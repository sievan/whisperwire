import MessagingContainer from "@/components/messaging/MessagingContainer";
import { Box, chakra, VStack } from "@chakra-ui/react";
import { Link, useParams } from "react-router";

function seededRandom(seed: number) {
  // Constants for LCG
  const a = 1103515245;
  const c = 12345;
  const m = Math.pow(2, 32);

  // Calculate the next number in the sequence
  const next = (a * seed + c) % m;

  // Normalize to [0, 1]
  return next / m;
}

const StylableLink = chakra(Link);

const LINK_SIZE = 16;

const ConversationLink = ({
  conversationId,
  isActive,
}: {
  conversationId: string;
  isActive: boolean;
}) => {
  const backgroundColor = `hsl(${
    seededRandom(parseInt(conversationId) || 1) * 360
  }, 70%, 70%)`;

  return (
    <StylableLink
      to={`/conversations/${conversationId}`}
      backgroundColor={backgroundColor}
      width={LINK_SIZE}
      height={LINK_SIZE}
      borderRadius="full"
      display="flex"
      alignItems="center"
      justifyContent="center"
      color="gray.800"
      fontSize="3xl"
      _hover={{
        color: "gray.800",
        fontWeight: "bold",
        opacity: 0.7,
      }}
      data-active={isActive}
      css={
        isActive && {
          boxShadow: "inset 3px 3px 10px -2px rgba(0,0,0,0.75)",
        }
      }
    >
      {conversationId}
    </StylableLink>
  );
};

function Conversations() {
  const { conversationId } = useParams();

  const conversations = [1, 2, 3, 4, 5].map((n) => n.toString());

  return (
    <Box
      width="100%"
      height="100%"
      display="flex"
      paddingY={8}
      paddingRight={8}
    >
      <VStack paddingX={2}>
        {conversations.map((id) => (
          <ConversationLink
            key={id}
            conversationId={id}
            isActive={id === conversationId}
          />
        ))}
      </VStack>
      <MessagingContainer flex={1} />
    </Box>
  );
}

export default Conversations;
