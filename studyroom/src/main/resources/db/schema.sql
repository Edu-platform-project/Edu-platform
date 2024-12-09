ALTER TABLE group_user
    DROP CONSTRAINT fkc6tpqk1plmbxmslbrn2kdm9i9;

ALTER TABLE group_user
    ADD CONSTRAINT fkc6tpqk1plmbxmslbrn2kdm9i9
        FOREIGN KEY (group_id) REFERENCES group_projects(gp_id) ON DELETE CASCADE;


CREATE OR REPLACE FUNCTION insert_into_group_approve()
    RETURNS TRIGGER AS $$
BEGIN
    -- value 값이 true로 변경될 때 동작
    IF NEW.group_valid = TRUE AND OLD.group_valid IS DISTINCT FROM TRUE THEN
        INSERT INTO group_approve (approve_id,group_id, lecture_id)
        VALUES (nextval('primary_sequence_ap_id'),NEW.gp_id, NEW.lecture_id_id);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_insert_into_group_approve
    AFTER UPDATE ON group_projects -- value를 변경하는 테이블 이름
    FOR EACH ROW
EXECUTE FUNCTION insert_into_group_approve();


CREATE OR REPLACE FUNCTION set_grade_based_on_stdId()
    RETURNS TRIGGER AS $$
BEGIN
    -- stdId 값에 따라 grade를 설정
    IF NEW.std_id = 1111 THEN
        NEW.grade := 'PROF';
    ELSIF NEW.std_id = 2222 THEN
        NEW.grade := 'TA';
    ELSIF NEW.std_id = 3333 THEN
        NEW.grade := 'LEAD';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거를 Users 테이블에 추가
CREATE TRIGGER set_grade_based_on_stdId_trigger
    BEFORE INSERT OR UPDATE ON Users
    FOR EACH ROW
EXECUTE FUNCTION set_grade_based_on_stdId();



-- CREATE OR REPLACE FUNCTION delete_related_approves()
--     RETURNS TRIGGER AS $$
-- BEGIN
--     DELETE FROM group_approve WHERE group_id = OLD.gp_id;
--     RETURN OLD;
-- END;
-- $$ LANGUAGE plpgsql;
-- CREATE TRIGGER trigger_delete_related_approves
--     AFTER DELETE ON group_projects
--     FOR EACH ROW
-- EXECUTE FUNCTION delete_related_approves();


