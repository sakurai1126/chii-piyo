import { describe, expect, it } from "vitest";

import { cn } from "@/utils/cn";

describe("cn utility", () => {
  it("クラス名が正しくマージされること", () => {
    const result = cn("px-2 py-1", "bg-blue-500", { "text-white": true });
    expect(result).toBe("px-2 py-1 bg-blue-500 text-white");
  });
});
