import Image from "next/image";
import { useRef } from "react";

import icon from "../assets/video-icon.svg";
import { useDragAndDrop } from "../hooks/useDragAndDrop";

type Props = {
  onFilesAdd: (files: File[]) => void;
};

export const VideoUploader = ({ onFilesAdd }: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { isDragging, handleDrop, handleDragEnter, handleDragLeave } = useDragAndDrop({
    onFilesAdd,
    acceptTypes: "video",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // ファイルが選択されたときにonFilesAddを呼び出しHooks内のuseStateにファイルとURLをセットする
    if (e.target.files) {
      // 動画ファイルのみをフィルタリングしてonFilesAddを呼び出す
      const files = [...e.target.files].filter((f) => f.type.startsWith("video/"));
      if (files.length) onFilesAdd(files);
    }
    // 同じファイルを連続で選択したときもonFilesAddが呼ばれるようにinputの値をリセットする
    e.target.value = "";
  };

  return (
    <section
      aria-label="動画のドラッグ&ドロップエリア"
      className={`bg-green-back border-green-accent relative block rounded-4xl border-2 border-dotted pt-10 pb-15 text-center max-md:rounded-2xl max-md:pb-5 ${isDragging ? "bg-white-back" : ""}`}
      onDrop={handleDrop}
      onDragOver={(e) => e.preventDefault()}
      onDragEnter={handleDragEnter}
      onDragLeave={handleDragLeave}
    >
      {/* 実態のinput要素を非表示にする */}
      <input
        ref={inputRef}
        type="file"
        accept="video/*"
        multiple
        className="hidden"
        onChange={handleChange}
      />

      <p className="text-brown-middle bg-accent-brown-back absolute top-5 right-8 grid h-8 w-25 place-content-center rounded-2xl border text-xs font-medium max-md:top-2 max-md:right-2 max-md:h-5 max-md:w-18 max-md:text-[10px]">
        一本ずつ
      </p>
      <Image
        src={icon}
        alt=""
        width={46}
        height={46}
        className="mx-auto max-md:h-8.5 max-md:w-8.5"
      />
      <p className="mt-5 text-xl font-medium max-md:mt-2.5 max-md:text-[13px]">
        動画をアップロード
      </p>
      <p className="text-note-gray mt-4 text-sm max-md:hidden">
        1ファイルずつ選択してください
        <br />
        アップロード完了後に次を追加できます
      </p>
      <button
        className="bg-green-accent hover:text-green-accent border-green-accent hover:bg-white-back mt-5 h-10 w-40 cursor-pointer rounded-4xl border font-medium text-white transition-all duration-500 max-md:hidden"
        onClick={() => inputRef.current?.click()}
      >
        動画を選択
      </button>
      <p className="text-note-gray mt-4 text-xs max-md:hidden">mp4 / mov - 1 本最大500MB</p>
      <p className="text-note-gray mt-2 text-[10px] leading-5 md:hidden">
        1ファイルずつ選択
        <br />
        1本最大500MB
      </p>
    </section>
  );
};
