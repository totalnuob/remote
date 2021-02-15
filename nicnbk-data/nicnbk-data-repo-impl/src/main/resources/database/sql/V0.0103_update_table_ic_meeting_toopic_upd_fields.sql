UPDATE ic_meeting_topic SET name=name_upd WHERE name_upd IS NOT NULL AND published_upd=true;
UPDATE ic_meeting_topic SET decision=decision_upd WHERE decision_upd IS NOT NULL AND published_upd=true;