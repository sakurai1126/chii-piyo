type Props = {
  isEasy: boolean;
  text: string;
};

export const PageTitle = ({ isEasy, text }: Readonly<Props>) => {
  return (
    <>
      <h1
        className={`font-title ${isEasy ? "text-center text-[26px]" : "text-[40px] @max-md:text-lg"}`}
      >
        {text}
      </h1>
      <div
        className={`mt-5 h-0.5 w-55 rounded-xs bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] @max-md:mt-2 @max-md:h-px @max-md:w-20 ${isEasy ? "mx-auto" : ""}`}
      ></div>
    </>
  );
};
