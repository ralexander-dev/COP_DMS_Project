import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Use polling for file watching — required for HMR inside Docker on macOS
  webpack(config, { dev }) {
    if (dev) {
      config.watchOptions = {
        poll: 1000,
        aggregateTimeout: 300,
      };
    }
    return config;
  },
  turbopack: {},
};

export default nextConfig;
