import { Button } from "@/components/ui/Button";

type Props = {
  onCancel: () => void;
  saveAction: () => void;
  isPending: boolean;
};

export const ModalButtons = ({ onCancel, saveAction, isPending }: Props) => {
  return (
    <div className="mt-10 flex h-full flex-col justify-between max-md:mt-6">
      <div className="flex justify-center gap-5">
        <Button variant="cancel" onClick={onCancel} disabled={isPending}>
          キャンセル
        </Button>
        <Button onClick={saveAction} disabled={isPending}>
          記録する
        </Button>
      </div>
    </div>
  );
};
