import { Button } from "@/components/ui/Button";

import { UploadImage } from "../types";

import { UploadFile } from "./UploadFile";

type Props = {
  items: UploadImage[];
  onRemove: (index: number) => void;
  onRemoveAll: () => void;
};

export const FileList = ({ items, onRemove, onRemoveAll }: Props) => {
  const totalSizeInKB = items.reduce((total, item) => total + item.file.size / 1024, 0);
  const totalSizeInMB = totalSizeInKB / 1024;
  return (
    <div className="mt-15 max-md:mt-10">
      <div className="flex items-start justify-between">
        <p className="text-xl font-medium max-md:text-sm">アップロードするファイル</p>
        <p className="text-note-gray pt-0.5 text-right max-md:text-xs">
          写真 : {items.length}枚 + 動画 : 1本
          <br />
          合計サイズ :{" "}
          {totalSizeInKB > 1024 ? `${totalSizeInMB.toFixed(1)}MB` : `${totalSizeInKB.toFixed(0)}KB`}
        </p>
      </div>
      <div className="mt-5 grid gap-5">
        {/* 各ファイル */}
        {items.map((item, index) => (
          <UploadFile key={item.previewUrl} item={item} onRemove={() => onRemove(index)} />
        ))}
      </div>
      <div className="border-t-line-gray mt-15 flex items-center justify-between border-t pt-10">
        <p className="">写真{items.length}枚と動画1本をアップロードします</p>
        <div className="flex gap-5">
          <Button variant="cancel" onClick={onRemoveAll}>
            キャンセル
          </Button>
          <Button variant="primary">アップロード</Button>
        </div>
      </div>
    </div>
  );
};
