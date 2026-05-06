import { UploadFile } from "./UploadFile";

export const FileList = () => {
  return (
    <div className="mt-15 max-md:mt-10">
      <div className="flex items-start justify-between">
        <p className="text-xl font-medium max-md:text-sm">アップロードするファイル</p>
        <p className="text-note-gray pt-0.5 text-right max-md:text-xs">
          写真 : 5枚 + 動画 : 1本
          <br />
          合計サイズ : 60.2MB
        </p>
      </div>
      <div className="mt-5 grid gap-5">
        {/* 各ファイル */}
        <UploadFile />
        <UploadFile />
      </div>
    </div>
  );
};
