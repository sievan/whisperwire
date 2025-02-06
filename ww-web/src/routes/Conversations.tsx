import MessagingContainer from "@/components/messaging/MessagingContainer";
import { Box, chakra, VStack } from "@chakra-ui/react";
import { Link, useParams } from "react-router";

// from http://www.w3.org/TR/WCAG20/#relativeluminancedef
function relativeLuminanceW3C(colorHex: string) {
  const ar = colorHex.match(/../g) || [];

  const [rNum, gNum, bNum] = ar.map((s) => parseInt(s, 16));

  const RsRGB = rNum / 255;
  const GsRGB = gNum / 255;
  const BsRGB = bNum / 255;

  const R =
    RsRGB <= 0.03928 ? RsRGB / 12.92 : Math.pow((RsRGB + 0.055) / 1.055, 2.4);
  const G =
    GsRGB <= 0.03928 ? GsRGB / 12.92 : Math.pow((GsRGB + 0.055) / 1.055, 2.4);
  const B =
    BsRGB <= 0.03928 ? BsRGB / 12.92 : Math.pow((BsRGB + 0.055) / 1.055, 2.4);

  // For the sRGB colorspace, the relative luminance of a color is defined as:
  const L = 0.2126 * R + 0.7152 * G + 0.0722 * B;

  return L;
}

function seededRandom(seed: number) {
  // Constants for LCG
  const a = 1103515245;
  const c = 12345;
  const m = Math.pow(2, 32);

  // Calculate the next number in the sequence
  const next = (a * seed + c) % m;

  // Normalize to [0.2, 1]
  return (next / m) * 0.8 + 0.2;
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
  const colorNumber = Math.floor(
    seededRandom(parseInt(conversationId) || 1) * 16777215,
  );

  const colorHexString = colorNumber.toString(16);
  const backgroundColor = "#" + colorHexString;
  const textColor =
    relativeLuminanceW3C(colorHexString) < 0.5 ? "white" : "gray.900";

  console.log(isActive);
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
      color={textColor}
      fontSize="3xl"
      _hover={{
        color: textColor,
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

  console.log(conversations);

  return (
    <Box
      width="100%"
      height="100%"
      display="flex"
      paddingY={8}
      paddingRight={8}
    >
      <VStack paddingX={2}>
        {conversations.map((link) => (
          <ConversationLink
            conversationId={link}
            isActive={link === conversationId}
          />
        ))}
      </VStack>
      <MessagingContainer flex={1} />
    </Box>
  );
}

export default Conversations;
