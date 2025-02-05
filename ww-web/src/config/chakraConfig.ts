import { createSystem, defaultConfig, defineConfig } from "@chakra-ui/react";

const customConfig = defineConfig({
  theme: {
    semanticTokens: {
      colors: {
        bodyText: { value: "{colors.gray.600}" },
        authorText: { value: "{colors.gray.500}" },
      },
    },
  },
});

export default createSystem(defaultConfig, customConfig);
