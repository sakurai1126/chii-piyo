import Image from "next/image";

import icon from "../assets/video-icon.svg";

export const VideoUploader = () => {
  return (
    <div className="bg-green-back border-green-accent relative block rounded-4xl border-2 border-dotted px-5 pt-10 pb-15 text-center max-md:rounded-2xl max-md:pb-5">
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
      <button className="bg-green-accent mt-5 h-10 w-40 rounded-4xl font-medium text-white max-md:hidden">
        動画を選択
      </button>
      <p className="text-note-gray mt-4 text-xs max-md:hidden">mp4 / mov - 1 本最大500MB</p>
      <p className="text-note-gray mt-2 text-[10px] leading-5 md:hidden">
        1ファイルずつ選択
        <br />
        1本最大500MB
      </p>
    </div>
  );
};
