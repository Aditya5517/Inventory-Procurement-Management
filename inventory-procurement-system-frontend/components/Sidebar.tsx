"use client";

import Link from "next/link";
import {
  usePathname,
  useRouter,
} from "next/navigation";

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  const links = [
    {
      name: "Dashboard",
      path: "/dashboard",
    },
    {
      name: "Products",
      path: "/products",
    },
    {
      name: "Categories",
      path: "/categories",
    },
    {
      name: "Suppliers",
      path: "/suppliers",
    },
    {
      name: "Purchase Requests",
      path: "/purchase-requests",
    },
    {
      name: "Purchase Orders",
      path: "/purchase-orders",
    },
    {
      name: "Goods Receipts",
      path: "/goods-receipts",
    },
    {
      name: "Stock Movements",
      path: "/stock-movements",
    },
    {
      name: "Notifications",
      path: "/notifications",
    },
  ];

  function handleLogout() {
    localStorage.removeItem("token");

    router.push("/login");
  }

  return (
    <aside className="flex min-h-screen w-64 flex-col border-r border-gray-200 bg-white p-5">
      <h1 className="mb-8 text-xl font-bold text-black">
        Inventory & Procurement
      </h1>

      <nav className="flex flex-col gap-2">
        {links.map((link) => (
          <Link
            key={link.path}
            href={link.path}
            className={`rounded px-3 py-2 ${
              pathname === link.path
                ? "bg-blue-50 text-blue-700"
                : "text-gray-700 hover:bg-gray-100"
            }`}
          >
            {link.name}
          </Link>
        ))}
      </nav>

      <button
        onClick={handleLogout}
        className="mt-auto rounded border border-gray-300 px-3 py-2 text-left text-red-600 hover:bg-gray-100"
      >
        Logout
      </button>
    </aside>
  );
}