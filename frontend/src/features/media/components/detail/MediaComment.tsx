"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useMemo, useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { type MediaCommentResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDate } from "@/utils/date";

import { createCommentAction } from "../../actions/createCommentAction";
import { deleteCommentAction } from "../../actions/deleteCommentAction";

type Props = {
  mediaId: number;
  users: UserResponseDto[];
  comments: MediaCommentResponseDto[];
  currentUser: UserResponseDto;
};

export const MediaComment = ({ mediaId, comments, currentUser, users }: Props) => {
  const [isCommentMode, setIsCommentMode] = useState<boolean>(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState<boolean>(false);
  const [deleteCommentId, setDeleteCommentId] = useState<number | null>(null);
  const [inputComment, setInputComment] = useState<string>("");
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  const userMap = useMemo(() => {
    const map = new Map<number, UserResponseDto>();
    users.forEach((user) => map.set(user.id, user));
    return map;
  }, [users]);

  const addComment = () => {
    if (!inputComment.trim()) {
      toast.error("コメントが入力されていません");
      return;
    }

    startTransition(async () => {
      const result = await createCommentAction({
        mediaId,
        content: inputComment,
      });

      if (result.success) {
        setInputComment("");
        toast.success("コメントを追加しました。");
      } else {
        toast.error(result.error);
      }
    });
  };

  const openDeleteModal = (id: number) => {
    setDeleteCommentId(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setDeleteCommentId(null);
  };

  const deleteComment = () => {
    if (!deleteCommentId) {
      toast.error("エラーが発生しました。");
      return;
    }

    startTransition(async () => {
      const result = await deleteCommentAction({
        commentId: deleteCommentId,
      });

      if (result.success) {
        closeDeleteModal();
        toast.success("コメントを削除しました。");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <p className="max-md:text-sm">コメント</p>
      {comments.length !== 0 || isCommentMode ? (
        <>
          <div className="border-brown-dark mt-2 rounded-lg border bg-[rgba(255,255,255,0.5)] px-4 py-6 backdrop-blur-[7.5px]">
            <div className="grid gap-6">
              {comments.map((comment) => (
                <div className="flex items-start justify-between max-md:flex-col" key={comment.id}>
                  <div className="flex items-start gap-4">
                    <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                      <Image
                        src={
                          userMap.get(comment.userId)?.presignedIconUrl || "/images/no-image.svg"
                        }
                        alt=""
                        width={31}
                        height={31}
                        className="h-full w-full rounded-full object-cover"
                      />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <p className="text-sm max-md:text-[13px]">{comment.displayName}</p>
                        <p className="text-xs text-gray-500 max-md:text-[11px]">
                          {formatJapaneseDate(comment.createdAt)}
                        </p>
                      </div>
                      <p className="mt-2 text-sm max-md:mt-1 max-md:text-xs">{comment.content}</p>
                    </div>
                  </div>
                  {comment.userId === currentUser.id && (
                    <button
                      className="text-warning cursor-pointer text-xs underline transition-all hover:opacity-70 max-md:mt-2 max-md:ml-auto max-md:text-[10px]"
                      onClick={() => openDeleteModal(comment.id)}
                      disabled={isPending}
                    >
                      コメントを削除する
                    </button>
                  )}
                </div>
              ))}
            </div>
            {/* 新規コメント */}
            <div className={`flex items-start gap-4 ${isCommentMode ? "" : "mt-10"}`}>
              <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <Image
                  src={currentUser.presignedIconUrl || "/images/no-image.svg"}
                  alt=""
                  width={31}
                  height={31}
                  className="h-full w-full rounded-full object-cover"
                />
              </div>
              <textarea
                placeholder="コメントを入力してください"
                className="border-line-gray focus:outline-brown-light min-h-20 w-full rounded-sm border bg-white p-2 text-sm max-md:text-xs"
                value={inputComment}
                onChange={(e) => setInputComment(e.target.value)}
              ></textarea>
            </div>
            <Button
              className="mt-4 ml-auto block max-md:h-9 max-md:w-28 max-md:text-xs"
              onClick={addComment}
              disabled={isPending}
            >
              コメントを追加する
            </Button>
          </div>

          <AnimatePresence>
            {isDeleteModalOpen && (
              <Modal>
                <ActionDialog onClose={closeDeleteModal}>
                  <div className="flex h-full flex-col justify-center">
                    <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                    <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                      選択したコメントを削除します。
                      <br />
                      本当によろしいですか？
                    </p>

                    <div className="flex justify-center gap-5">
                      <Button variant="cancel" onClick={closeDeleteModal} disabled={isPending}>
                        キャンセル
                      </Button>
                      <Button variant="remove" disabled={isPending} onClick={deleteComment}>
                        削除する
                      </Button>
                    </div>
                  </div>
                </ActionDialog>
              </Modal>
            )}
          </AnimatePresence>
        </>
      ) : (
        <Button
          className="mt-4 block max-md:h-9 max-md:w-28 max-md:text-xs"
          onClick={() => setIsCommentMode(true)}
        >
          コメントする
        </Button>
      )}
    </>
  );
};
