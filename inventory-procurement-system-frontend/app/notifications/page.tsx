"use client";

import {
  useEffect,
  useState,
} from "react";

import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Notification = {
  id: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  relatedEntityType: string;
  relatedEntityId: number;
  createdAt: string;
  readAt: string | null;
};

export default function NotificationsPage() {
  const [
    notifications,
    setNotifications,
  ] = useState<Notification[]>([]);

  useEffect(() => {
    loadNotifications();
  }, []);

  async function loadNotifications() {
    try {
      const data =
        await apiRequest(
          "/api/notifications"
        );

      setNotifications(data);
    } catch (error) {
      console.error(error);
    }
  }

  async function markAsRead(
    id: number
  ) {
    try {
      await apiRequest(
        `/api/notifications/${id}/read`,
        {
          method: "PATCH",
        }
      );

      loadNotifications();
    } catch (error) {
      console.error(error);

      alert(
        "Could not update notification"
      );
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Notifications
      </h1>

      <p className="mt-2 text-gray-600">
        System notifications
      </p>

      <div className="mt-6 max-w-3xl space-y-3">
        {notifications.map(
          (notification) => (
            <div
              key={notification.id}
              className={`rounded-lg border p-4 ${
                notification.isRead
                  ? "bg-white"
                  : "bg-blue-50"
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h2 className="font-semibold text-black">
                    {
                      notification.title
                    }
                  </h2>

                  <p className="mt-1 text-gray-600">
                    {
                      notification.message
                    }
                  </p>

                  <p className="mt-2 text-xs text-gray-400">
                    {notification.createdAt
                      ? new Date(
                          notification.createdAt
                        ).toLocaleString()
                      : ""}
                  </p>
                </div>

                {!notification.isRead && (
                  <button
                    onClick={() =>
                      markAsRead(
                        notification.id
                      )
                    }
                    className="whitespace-nowrap rounded border border-gray-300 px-3 py-1 text-sm text-black hover:bg-gray-100"
                  >
                    Mark as Read
                  </button>
                )}
              </div>
            </div>
          )
        )}

        {notifications.length === 0 && (
          <div className="rounded-lg border bg-white p-6 text-gray-500">
            No notifications.
          </div>
        )}
      </div>
    </AppLayout>
  );
}