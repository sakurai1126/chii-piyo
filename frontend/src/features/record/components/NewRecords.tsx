"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { AddMediaModal } from "@/features/media";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic } from "@/utils/date";

import { createFirstRecordAction } from "../actions/createFirstRecordAction";
import arrow from "../assets/arrow.svg";
import closeIcon from "../assets/close.svg";
import mediaIcon from "../assets/media.svg";
import plusIcon from "../assets/plus.svg";
import { FirstRecordData, SelectedMediaData } from "../types";

type Props = {
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
};

export const NewRecords = ({ tags, sharingGroups }: Props) => {
  // メニュー開閉フラグ
  const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

  // 保存時確認モーダル
  const [isSaveConfirmOpen, setIsSaveConfirmOpen] = useState<boolean>(false);

  // メディア追加モーダル
  const [isAddMediaModalOpen, setIsAddMediaModalOpen] = useState<boolean>(false);

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // メディア追加の状態管理
  const [selectedMediaData, setSelectedMediaData] = useState<SelectedMediaData[]>([]);

  // 記録内容
  const initialData = {
    title: "",
    achievedDate: formatJapaneseDateBasic(new Date()),
    comment: "",
  };
  const [data, setData] = useState<FirstRecordData>(initialData);

  // 確認画面の表示と表示前バリデーション
  const confirmOpen = () => {
    if (!data.title) {
      toast.error("記録内容を入力してください");
      return;
    }

    if (!data.achievedDate) {
      toast.error("日時を入力してください");
      return;
    }

    setIsSaveConfirmOpen(true);
  };

  // 保存処理
  const saveAction = () => {
    startTransition(async () => {
      const result = await createFirstRecordAction({
        title: data.title,
        achievedDate: new Date(data.achievedDate),
        comment: data.comment,
        mediaIds: selectedMediaData.map((media) => media.id),
      });

      if (result.success) {
        setIsMenuOpen(false);
        setIsSaveConfirmOpen(false);
        setData(initialData);
        setSelectedMediaData([]);
        toast.success("はじめて記録を作成しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  // 編集キャンセル処理
  const cancelEdit = () => {
    setIsMenuOpen(false);
    setIsSaveConfirmOpen(false);
    setData(initialData);
    setSelectedMediaData([]);
  };

  return (
    <div className="border-brown-dark bg-white-back mt-12 rounded-lg border border-dashed max-md:mt-6">
      {/* 開くボタン */}
      <AccordionContent isOpen={!isMenuOpen}>
        <button
          className="hover:bg-white-back bg-green-back flex h-20 w-full cursor-pointer items-center justify-center gap-3 rounded-lg transition-all"
          onClick={() => setIsMenuOpen(true)}
        >
          <p className="text-brown-light font-medium max-md:text-[13px]">
            新しいはじめてを記録する
          </p>
          <Image src={plusIcon} alt="" width={14} height={14} />
        </button>
      </AccordionContent>

      {/* 新規追加UI */}
      <AccordionContent isOpen={isMenuOpen}>
        <div className="px-7 pt-7 max-md:px-4 max-md:pt-5">
          <div className="flex gap-7 max-md:flex-col max-md:gap-4">
            <div className="grid gap-2">
              <p className="max-md:text-[13px]">記録内容</p>
              <input
                type="text"
                className="focus:outline-brown-light border-line-gray h-12 w-100 rounded-sm border bg-white px-2 max-md:h-9 max-md:w-full max-md:max-w-100 max-md:text-[13px]"
                value={data.title}
                onChange={(e) => setData({ ...data, title: e.target.value })}
              />
            </div>
            <div className="grid gap-2">
              <p className="max-md:text-[13px]">日付</p>
              <input
                type="date"
                className="focus:outline-brown-light border-line-gray h-12 w-40 rounded-sm border bg-white px-2 max-md:h-9 max-md:text-[13px]"
                value={data.achievedDate}
                onChange={(e) => setData({ ...data, achievedDate: e.target.value })}
              />
            </div>
          </div>
          <p className="mt-5 max-md:text-[13px]">コメント</p>
          <textarea
            className="focus:outline-brown-light border-line-gray mt-2 h-25 w-full rounded-sm border bg-white p-2 max-md:h-20 max-md:text-[13px]"
            value={data.comment}
            onChange={(e) => setData({ ...data, comment: e.target.value })}
          ></textarea>
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
                    onClick={() =>
                      setSelectedMediaData((prev) =>
                        prev.filter((prevItem) => prevItem.id !== item.id),
                      )
                    }
                  >
                    <Image src={closeIcon} alt="" width={8} height={8} />
                  </button>
                </div>
              ))}
            </div>
          </div>
          <div className="mt-5 ml-auto flex w-fit gap-5 max-md:mt-7">
            <Button variant="cancel" onClick={cancelEdit}>
              キャンセル
            </Button>
            <Button onClick={confirmOpen}>記録する</Button>
          </div>
        </div>
        {/* 閉じるボタン */}
        <button
          className="mt-3 grid w-full cursor-pointer place-content-center p-3 pb-5"
          onClick={() => setIsMenuOpen(false)}
        >
          <Image src={arrow} alt="" className="" width={13} height={7} />
        </button>
      </AccordionContent>

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
        message="新しい記録を作成します。"
        buttonMessage="記録する"
      />
    </div>
  );
};
