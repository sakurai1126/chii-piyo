import { Button } from "@/components/ui/Button";

export const TrashAllDelete = () => {
  return (
    <div className="border-line-gray mt-10 flex items-center justify-between border-t pt-7 max-md:flex-col max-md:items-start">
      <div>
        <p className="text-warning text-sm max-md:text-xs">ゴミ箱を空にする</p>
        <p className="mt-2 text-sm max-md:text-xs">
          すべてのメディアを完全に削除します。
          <br className="md:hidden" />
          この操作は取り消せません。
        </p>
      </div>
      <Button variant="remove" className="w-fit px-6 max-md:mt-4 max-md:ml-auto">
        ゴミ箱を空にする
      </Button>
    </div>
  );
};
