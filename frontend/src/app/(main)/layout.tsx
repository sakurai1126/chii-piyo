import BackGround from "@/components/layout/BackGround";
import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import BottomNavigation from "@/components/layout/BottomNavigation";

export default function MainLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <BackGround>
      <Header />
      {children}
      <BottomNavigation />
      <Footer />
    </BackGround>
  );
}
