import { beforeEach, describe, expect, it, vi } from "vitest";

import { convertHeicToJpeg, isHeicFile, isImageFile } from "./convertHeicToJpeg";

// モックの定義
const heicToMock = vi.hoisted(() => vi.fn());
vi.mock("heic-to/csp", () => ({ heicTo: heicToMock }));

describe("convertHeicToJpeg", () => {
  beforeEach(() => {
    // モックが常にJPEGを返すように設定
    heicToMock.mockReset();
    heicToMock.mockResolvedValue(new Blob(["jpeg"], { type: "image/jpeg" }));
  });

  it("Convert-01: HEICをJPEGに変換し、拡張子とMIMEタイプがJPEGになること", async () => {
    const file = new File(["heic"], "IMG_0001.HEIC", { type: "image/heic" });

    const result = await convertHeicToJpeg(file);

    expect(result.name).toBe("IMG_0001.jpg");
    expect(result.type).toBe("image/jpeg");
  });

  it("Convert-02: typeが空でも拡張子がheicなら変換されること", async () => {
    const file = new File(["heic"], "IMG_0002.heic", { type: "" });

    const result = await convertHeicToJpeg(file);

    expect(result.type).toBe("image/jpeg");
    expect(result.name).toBe("IMG_0002.jpg");
  });

  it("Convert-03: HEIC以外のファイルは変換せずそのまま返すこと", async () => {
    const file = new File(["png"], "image.png", { type: "image/png" });

    const result = await convertHeicToJpeg(file);

    expect(result).toBe(file);
    expect(heicToMock).not.toHaveBeenCalled();
  });

  it("Convert-04: isImageFileは画像またはtypeが空のHEICを画像として扱い、画像以外は除外すること", () => {
    expect(isImageFile(new File(["x"], "a.heic", { type: "" }))).toBe(true);
    expect(isImageFile(new File(["x"], "a.png", { type: "image/png" }))).toBe(true);
    expect(isImageFile(new File(["x"], "a.mp4", { type: "video/mp4" }))).toBe(false);
  });

  it("Convert-05: isHeicFileはMIMEタイプまたは拡張子がHEIC/HEIFの場合のみtrueを返すこと", () => {
    // MIMEタイプで判定される場合
    expect(isHeicFile(new File(["x"], "a.dat", { type: "image/heic" }))).toBe(true);
    expect(isHeicFile(new File(["x"], "a.dat", { type: "image/heif" }))).toBe(true);
    // typeが空でも拡張子で判定される場合
    expect(isHeicFile(new File(["x"], "a.HEIC", { type: "" }))).toBe(true);
    expect(isHeicFile(new File(["x"], "a.heif", { type: "" }))).toBe(true);
    // HEIC/HEIFではない場合
    expect(isHeicFile(new File(["x"], "a.jpg", { type: "image/jpeg" }))).toBe(false);
  });
});
