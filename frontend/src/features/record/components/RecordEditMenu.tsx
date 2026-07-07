import Image from "next/image";
import { Dispatch, SetStateAction } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { AddMediaModal } from "@/features/media";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

import closeIcon from "../assets/close.svg";
import mediaIcon from "../assets/media.svg";
import { useRecordEdit } from "../hooks/useRecordEdit";
import { RecordData } from "../types";

type Props = {
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  setIsMenuOpen: Dispatch<SetStateAction<boolean>>;
  initialEditData?: RecordData;
  variant: "newFirstRecord" | "editFirstRecord" | "newWordRecord" | "editWordRecord";
};
export const RecordEditMenu = ({
  tags,
  sharingGroups,
  setIsMenuOpen,
  initialEditData,
  variant,
}: Props) => {
  const {
    isSaveConfirmOpen,
    isAddMediaModalOpen,
    setIsAddMediaModalOpen,
    isPending,
    selectedMediaData,
    setSelectedMediaData,
    setIsSaveConfirmOpen,
    data,
    setData,
    confirmOpen,
    removeMedia,
    cancelEdit,
    saveAction,
  } = useRecordEdit({
    setIsMenuOpen,
    initialEditData,
    variant,
  });
  return (
    <>
      {/* 記録タイトルと日付入力 */}
      <div className="flex gap-7 max-md:flex-col max-md:gap-4">
        <div className="grid gap-2">
          <p className="max-md:text-[13px]">
            {(variant === "newWordRecord" || variant === "editWordRecord") && <>おぼえたことば</>}
            {(variant === "newFirstRecord" || variant === "editFirstRecord") && <>記録内容</>}
          </p>
          <input
            type="text"
            className="focus:outline-brown-light border-line-gray bg-light-dark h-12 w-100 rounded-sm border px-2 max-md:h-9 max-md:w-full max-md:max-w-100 max-md:text-[13px] dark:outline-none"
            value={data.title}
            onChange={(e) => setData({ ...data, title: e.target.value })}
          />
        </div>
        <div className="grid gap-2">
          <p className="max-md:text-[13px]">日付</p>
          <input
            type="date"
            className="focus:outline-brown-light border-line-gray bg-light-dark h-12 w-40 rounded-sm border px-2 max-md:h-9 max-md:text-[13px] dark:outline-none"
            value={data.recordedDate}
            onChange={(e) => setData({ ...data, recordedDate: e.target.value })}
          />
        </div>
      </div>

      {/* コメント入力 */}
      <p className="mt-5 max-md:text-[13px]">コメント</p>
      <textarea
        className="focus:outline-brown-light border-line-gray bg-light-dark mt-2 h-25 w-full rounded-sm border p-2 max-md:h-20 max-md:text-[13px] dark:outline-none"
        value={data.comment}
        onChange={(e) => setData({ ...data, comment: e.target.value })}
      ></textarea>

      {/* メディア追加 */}
      <div className="mt-7 flex gap-5 max-md:mt-5 max-md:flex-col">
        <AccentButton
          variant="button"
          className="shrink-0"
          onClick={() => setIsAddMediaModalOpen(true)}
        >
          <span>メディアを追加</span>
          <Image src={mediaIcon} alt="" width={16} height={16} />
        </AccentButton>
        <div className="flex flex-wrap gap-3">
          {selectedMediaData.map((item) => (
            <div className="group relative" key={item.id}>
              <Image
                src={item.url}
                alt=""
                className="rounded-sm max-md:h-12 max-md:w-12"
                width={80}
                height={80}
              />
              <button
                className="bg-warning absolute -top-0.5 -right-0.5 hidden h-4 w-4 cursor-pointer place-content-center rounded-2xl group-hover:grid"
                onClick={() => removeMedia(item.id)}
              >
                <Image src={closeIcon} alt="" width={8} height={8} />
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* ボタン */}
      <div className="mt-5 ml-auto flex w-fit gap-5 max-md:mt-7">
        <Button variant="cancel" onClick={cancelEdit}>
          キャンセル
        </Button>
        <Button onClick={confirmOpen}>
          {variant === "newFirstRecord" || variant === "newWordRecord" ? "記録する" : "更新する"}
        </Button>
      </div>

      {/* モーダル */}
      <AddMediaModal
        tags={tags}
        sharingGroups={sharingGroups}
        isOpen={isAddMediaModalOpen}
        setIsOpen={setIsAddMediaModalOpen}
        selectedMediaData={selectedMediaData}
        setSelectedMediaData={setSelectedMediaData}
        variant="record"
      />

      <ConfirmModal
        isOpen={isSaveConfirmOpen}
        isPending={isPending}
        action={saveAction}
        closeAction={() => setIsSaveConfirmOpen(false)}
        message={
          variant === "newFirstRecord" || variant === "newWordRecord"
            ? "新しい記録を作成します。"
            : "記録を更新します。"
        }
        buttonMessage={
          variant === "newFirstRecord" || variant === "newWordRecord" ? "記録する" : "更新する"
        }
      />
    </>
  );
};
